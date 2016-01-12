package com.gu.identity.frontend.controllers

import com.gu.identity.frontend.models.{ClientRegistrationIp, TrackingData}
import com.gu.identity.frontend.services.{ServiceGatewayError, ServiceBadRequest, IdentityService}
import com.gu.identity.frontend.utils.UrlDecoder
import com.gu.identity.service.client.RegisterResponseUser
import org.mockito.Matchers.{any => argAny, eq => argEq, _}
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.MessagesApi
import play.api.mvc.Cookie
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.{ExecutionContext, Future}

class RegisterActionSpec extends PlaySpec with MockitoSugar {

  trait WithControllerMockedDependencies {
    val mockIdentityService = mock[IdentityService]
    val messages = mock[MessagesApi]
    val controller = new RegisterAction(mockIdentityService, messages)
  }

  def fakeRegisterRequest(
     firstName: String = "first",
     lastName: String = "last",
     email: String = "test@email.com",
     username: String = "username",
     password: String = "password",
     receiveGnmMarketing: Boolean = true,
     receive3rdPartyMarketing: Boolean = true,
     returnUrl: Option[String] = Some("http://www.theguardian.com"),
     skipConfirmation: Option[Boolean] = None) = {
    val bodyParams = Seq(
      "firstName" -> firstName,
      "lastName" -> lastName,
      "email" -> email,
      "username" -> username,
      "password" -> password,
      "receiveGnmMarketing" -> receiveGnmMarketing.toString,
      "receive3rdPartyMarketing" -> receive3rdPartyMarketing.toString,
      "returnUrl" -> returnUrl.getOrElse("http://none.com"),
      "skipConfirmation" -> skipConfirmation.getOrElse(false).toString)

    FakeRequest("POST", "/actions/register").withFormUrlEncodedBody(bodyParams: _*)
  }

  def fakeRegisterThenSignIn(mockIdentityService: IdentityService) =
    mockIdentityService.registerThenSignIn(anyObject(), argAny[ClientRegistrationIp], argAny[TrackingData])(argAny[ExecutionContext])

  "POST /register" should {
    "redirect to theguardian.com and sign user in when registration is successful" in new WithControllerMockedDependencies {
      val returnUrl = Some("http://www.theguardian.com/test")
      val testCookie = Cookie("SC_GU_U", "##hash##")

      when(fakeRegisterThenSignIn(mockIdentityService))
      .thenReturn{
        Future.successful(
          Right(Seq(testCookie))
        )
      }

      val result = call(controller.register, fakeRegisterRequest(returnUrl = returnUrl))
      val resultCookies = cookies(result)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustEqual returnUrl
      resultCookies.size mustEqual 1
      resultCookies.head mustEqual testCookie
    }

    "redirect to register page when failed to create account (Service Bad Request)" in new WithControllerMockedDependencies {
      when(fakeRegisterThenSignIn(mockIdentityService))
        .thenReturn{
          Future.successful {
            Left(Seq(ServiceBadRequest("User could not be registered, invalid fields in form.")))
          }
        }

      val result = call(controller.register, fakeRegisterRequest())
      val queryParams = UrlDecoder.getQueryParams(redirectLocation(result).get)

      status(result) mustEqual SEE_OTHER

      queryParams.contains("error") mustEqual true
      queryParams.get("error") mustEqual Some("register-error-bad-request")

      redirectLocation(result).get must startWith (routes.Application.register(Seq.empty, None).url)
    }

    "redirect to register page when service error (Service Gateway Error)" in new WithControllerMockedDependencies {
      when(fakeRegisterThenSignIn(mockIdentityService))
        .thenReturn{
          Future.successful {
            Left(Seq(ServiceGatewayError("Unexpected 500 error")))
          }
        }

      val result = call(controller.register, fakeRegisterRequest())
      val queryParams = UrlDecoder.getQueryParams(redirectLocation(result).get)

      status(result) mustEqual SEE_OTHER

      queryParams.contains("error") mustEqual true
      queryParams.get("error") mustEqual Some("register-error-gateway")

      redirectLocation(result).get must startWith (routes.Application.register(Seq.empty, None).url)
    }

    "redirect to register page when error from the future" in new WithControllerMockedDependencies {
      when(fakeRegisterThenSignIn(mockIdentityService))
        .thenReturn{
          Future.failed {
            new RuntimeException("Unexpected 500 error")
          }
        }

      val result = call(controller.register, fakeRegisterRequest())
      val queryParams = UrlDecoder.getQueryParams(redirectLocation(result).get)

      status(result) mustEqual SEE_OTHER

      queryParams.contains("error") mustEqual true
      queryParams.get("error") mustEqual Some("register-error-gateway")

      redirectLocation(result).get must startWith (routes.Application.register(Seq.empty, None).url)
    }

    "include skip confirmation in params for failed registration redirect if the value is specified on the request" in new WithControllerMockedDependencies {
      val skipConfirmation = Some(true)
      val testCookie = Cookie("SC_GU_U", "##hash##")

      when(fakeRegisterThenSignIn(mockIdentityService))
        .thenReturn{
          Future.successful(
            Left(Seq(ServiceGatewayError("Unexpected 500 error")))
          )
        }

      val result = call(controller.register, fakeRegisterRequest(skipConfirmation = skipConfirmation))
      val queryParams = UrlDecoder.getQueryParams(redirectLocation(result).get)

      queryParams.contains("skipConfirmation") mustEqual true
      queryParams.get("skipConfirmation") mustEqual Some("true")
    }
  }
}
