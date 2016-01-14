package com.gu.identity.frontend.controllers

import java.net.{URLDecoder, URLEncoder}

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.models.{ClientRegistrationIp, TrackingData}
import com.gu.identity.frontend.services.{ServiceGatewayError, ServiceBadRequest, IdentityService}
import com.gu.identity.frontend.utils.UrlDecoder
import org.mockito.Matchers.{any => argAny, _}
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.MessagesApi
import play.api.{Configuration => PlayConfiguration}
import play.api.mvc.Cookie
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.utils.UriEncoding

import scala.concurrent.{ExecutionContext, Future}

class RegisterActionSpec extends PlaySpec with MockitoSugar {

  trait WithControllerMockedDependencies {
    val mockIdentityService = mock[IdentityService]
    val messages = mock[MessagesApi]
    val config = new Configuration {
      override val identityApiHost: String = "identityApiHost"
      override val identityApiKey: String = "identityApiKey"
      override val identityCookieDomain: String = "theguardian.com"
      override val identityProfileBase: String = "profile.theguardian.com"
      override val omnitureAccount: String = "omnitureAccount"
      override val appConfiguration: PlayConfiguration = null
    }
    val controller = new RegisterAction(mockIdentityService, messages, config)
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
     skipConfirmation: Option[Boolean] = None,
     group: Option[String] = None) = {
    val bodyParams = Seq(
      "firstName" -> firstName,
      "lastName" -> lastName,
      "email" -> email,
      "username" -> username,
      "password" -> password,
      "receiveGnmMarketing" -> receiveGnmMarketing.toString,
      "receive3rdPartyMarketing" -> receive3rdPartyMarketing.toString,
      "returnUrl" -> returnUrl.getOrElse("http://none.com"),
      "skipConfirmation" -> skipConfirmation.getOrElse(false).toString,
      "group" -> group.getOrElse(""))

    FakeRequest("POST", "/actions/register").withFormUrlEncodedBody(bodyParams: _*)
  }

  def fakeRegisterThenSignIn(mockIdentityService: IdentityService) =
    mockIdentityService.registerThenSignIn(anyObject(), argAny[ClientRegistrationIp], argAny[TrackingData])(argAny[ExecutionContext])

  "POST /register" should {
    "redirect to theguardian.com and sign user in when registration is successful skipConfirmation is true and no group code" in new WithControllerMockedDependencies {
      val returnUrl = Some("http://www.theguardian.com/test?returnUrl=www.bbc.co.uk?skipConfirmation=true&skipConfirmation=false")
      val testCookie = Cookie("SC_GU_U", "##hash##")
      val skipConfirmation = Some(true)

      when(fakeRegisterThenSignIn(mockIdentityService))
      .thenReturn{
        Future.successful(
          Right(Seq(testCookie))
        )
      }

      val result = call(controller.register, fakeRegisterRequest(returnUrl = returnUrl, skipConfirmation = skipConfirmation))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustEqual returnUrl
    }

    "have a cookie when registration is successful skipConfirmation is true and no group code" in new WithControllerMockedDependencies {
      val returnUrl = Some("http://www.theguardian.com/test")
      val testCookie = Cookie("SC_GU_U", "##hash##")
      val skipConfirmation = Some(true)

      when(fakeRegisterThenSignIn(mockIdentityService))
        .thenReturn{
          Future.successful(
            Right(Seq(testCookie))
          )
        }

      val result = call(controller.register, fakeRegisterRequest(returnUrl = returnUrl, skipConfirmation = skipConfirmation))
      val resultCookies = cookies(result)

      resultCookies.size mustEqual 1
      resultCookies.head mustEqual testCookie
    }

    "redirect to confirmation page when registration is successful skipConfirmation is false and no group code" in new WithControllerMockedDependencies {
      val testCookie = Cookie("SC_GU_U", "##hash##")
      val skipConfirmation = Some(false)

      when(fakeRegisterThenSignIn(mockIdentityService))
        .thenReturn{
          Future.successful(
            Right(Seq(testCookie))
          )
        }

      val result = call(controller.register, fakeRegisterRequest(skipConfirmation = skipConfirmation))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).get must startWith (routes.Application.confirm().url)
    }

    "have a sign in cookie when registration is successful skipConfirmation is false and no group code" in new WithControllerMockedDependencies {
      val testCookie = Cookie("SC_GU_U", "##hash##")
      val skipConfirmation = Some(false)

      when(fakeRegisterThenSignIn(mockIdentityService))
        .thenReturn{
          Future.successful(
            Right(Seq(testCookie))
          )
        }

      val result = call(controller.register, fakeRegisterRequest(skipConfirmation = skipConfirmation))
      val resultCookies = cookies(result)

      resultCookies.size mustEqual 1
      resultCookies.head mustEqual testCookie
    }

    "include a return url when registration is successful skipConfirmation is false and no group code" in new WithControllerMockedDependencies {
      val returnUrl = "http://www.theguardian.com/test?returnUrl=www.bbc.co.uk?skipConfirmation=true&skipConfirmation=false"
      val testCookie = Cookie("SC_GU_U", "##hash##")
      val skipConfirmation = Some(false)

      when(fakeRegisterThenSignIn(mockIdentityService))
        .thenReturn{
          Future.successful(
            Right(Seq(testCookie))
          )
        }

      val result = call(controller.register, fakeRegisterRequest(returnUrl = Some(returnUrl), skipConfirmation = skipConfirmation))
      val queryParams = UrlDecoder.getQueryParams(redirectLocation(result).get)

      queryParams.contains("returnUrl") mustEqual true
      val rtn = queryParams.get("returnUrl").get
      val url = URLDecoder.decode(URLDecoder.decode(rtn, "UTF-8"),"UTF-8")
      url mustEqual returnUrl
    }

    "redirect to 3rd party T&Cs page when registration is successful skipConfirmation is true and group code is valid" in new WithControllerMockedDependencies {
      val testCookie = Cookie("SC_GU_U", "##hash##")
      val skipConfirmation = Some(true)
      val group = Some("GRS")

      when(fakeRegisterThenSignIn(mockIdentityService))
        .thenReturn{
          Future.successful(
            Right(Seq(testCookie))
          )
        }

      val result = call(controller.register, fakeRegisterRequest(skipConfirmation = skipConfirmation, group = group))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).get must startWith (s"${config.identityProfileBase}/agree/${group.get}")
    }

    "have a sign in cookie when registration is successful skipConfirmation is true and group code is valid" in new WithControllerMockedDependencies {
      val testCookie = Cookie("SC_GU_U", "##hash##")
      val skipConfirmation = Some(true)
      val group = Some("GRS")

      when(fakeRegisterThenSignIn(mockIdentityService))
        .thenReturn{
          Future.successful(
            Right(Seq(testCookie))
          )
        }

      val result = call(controller.register, fakeRegisterRequest(skipConfirmation = skipConfirmation, group = group))
      val resultCookies = cookies(result)

      resultCookies.size mustEqual 1
      resultCookies.head mustEqual testCookie
    }

    "include a return url when registration is successful skipConfirmation is true and group code is valid" in new WithControllerMockedDependencies {
      val returnUrl = "http://www.theguardian.com/test?returnUrl=www.bbc.co.uk?skipConfirmation=true&skipConfirmation=false"
      val skipConfirmation = Some(true)
      val group = Some("GRS")
      val testCookie = Cookie("SC_GU_U", "##hash##")

      when(fakeRegisterThenSignIn(mockIdentityService))
        .thenReturn{
          Future.successful(
            Right(Seq(testCookie))
          )
        }

      val result = call(controller.register, fakeRegisterRequest(returnUrl=Some(returnUrl), skipConfirmation = skipConfirmation, group = group))
      val queryParams = UrlDecoder.getQueryParams(redirectLocation(result).get)

      queryParams.contains("returnUrl") mustEqual true
      val url = URLDecoder.decode(queryParams.get("returnUrl").get, "UTF-8")
      url mustEqual returnUrl
    }

    "include skipConfirmation param when registration is successful skipConfirmation is true and group code is valid" in new WithControllerMockedDependencies {
      val returnUrl = Some("http://www.theguardian.com/test")
      val skipConfirmation = Some(true)
      val group = Some("GRS")
      val testCookie = Cookie("SC_GU_U", "##hash##")

      when(fakeRegisterThenSignIn(mockIdentityService))
        .thenReturn{
          Future.successful(
            Right(Seq(testCookie))
          )
        }

      val result = call(controller.register, fakeRegisterRequest(returnUrl=returnUrl, skipConfirmation = skipConfirmation, group = group))
      val queryParams = UrlDecoder.getQueryParams(redirectLocation(result).get)

      queryParams.contains("skipConfirmation") mustEqual true
      queryParams.get("skipConfirmation") mustEqual Some("true")
    }

    "include skipThirdPartyLandingPage param when registration is successful skipConfirmation is true and group code is valid" in new WithControllerMockedDependencies {
      val returnUrl = Some("http://www.theguardian.com/test")
      val skipConfirmation = Some(true)
      val group = Some("GRS")
      val testCookie = Cookie("SC_GU_U", "##hash##")

      when(fakeRegisterThenSignIn(mockIdentityService))
        .thenReturn{
          Future.successful(
            Right(Seq(testCookie))
          )
        }

      val result = call(controller.register, fakeRegisterRequest(returnUrl=returnUrl, skipConfirmation = skipConfirmation, group = group))
      val queryParams = UrlDecoder.getQueryParams(redirectLocation(result).get)

      queryParams.contains("skipThirdPartyLandingPage") mustEqual true
      queryParams.get("skipThirdPartyLandingPage") mustEqual Some("true")
    }

    "redirect to register confirmation page when registration is successful skipConfirmation is false and group code is valid" in new WithControllerMockedDependencies {
      val testCookie = Cookie("SC_GU_U", "##hash##")
      val skipConfirmation = Some(false)
      val group = Some("GRS")

      when(fakeRegisterThenSignIn(mockIdentityService))
        .thenReturn{
          Future.successful(
            Right(Seq(testCookie))
          )
        }

      val result = call(controller.register, fakeRegisterRequest(skipConfirmation = skipConfirmation, group = group))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).get must startWith (s"${config.identityProfileBase}/agree/${group.get}")
    }

    "have a sign in cookie when registration is successful skipConfirmation is false and group code is valid" in new WithControllerMockedDependencies {
      val testCookie = Cookie("SC_GU_U", "##hash##")
      val skipConfirmation = Some(false)
      val group = Some("GRS")

      when(fakeRegisterThenSignIn(mockIdentityService))
        .thenReturn{
          Future.successful(
            Right(Seq(testCookie))
          )
        }

      val result = call(controller.register, fakeRegisterRequest(skipConfirmation = skipConfirmation, group = group))
      val resultCookies = cookies(result)

      resultCookies.size mustEqual 1
      resultCookies.head mustEqual testCookie
    }

    "include a return url when registration is successful skipConfirmation is false and group code is valid" in new WithControllerMockedDependencies {
      val returnUrl = Some("http://www.theguardian.com/test")
      val skipConfirmation = Some(false)
      val group = Some("GRS")
      val testCookie = Cookie("SC_GU_U", "##hash##")

      when(fakeRegisterThenSignIn(mockIdentityService))
        .thenReturn{
          Future.successful(
            Right(Seq(testCookie))
          )
        }

      val result = call(controller.register, fakeRegisterRequest(returnUrl=returnUrl, skipConfirmation = skipConfirmation, group = group))
      val queryParams = UrlDecoder.getQueryParams(redirectLocation(result).get)
      queryParams.contains("returnUrl") mustEqual true
      val url = UriEncoding.decodePath(queryParams.get("returnUrl").get, "UTF-8")
      url must startWith(routes.Application.confirm().url)
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

    "include group in params for failed registration redirect if the value is specified on the request" in new WithControllerMockedDependencies {
      val group = Some("ABC")

      when(fakeRegisterThenSignIn(mockIdentityService))
        .thenReturn{
          Future.successful(
            Left(Seq(ServiceGatewayError("Unexpected 500 error")))
          )
        }

      val result = call(controller.register, fakeRegisterRequest(group = group))
      val queryParams = UrlDecoder.getQueryParams(redirectLocation(result).get)

      queryParams.contains("group") mustEqual true
      queryParams.get("group") mustEqual group
    }
  }
}
