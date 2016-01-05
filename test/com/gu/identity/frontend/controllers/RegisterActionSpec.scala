package com.gu.identity.frontend.controllers

import com.gu.identity.frontend.services.IdentityService
import org.mockito.Matchers.{any => argAny}
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.MessagesApi
import play.api.mvc.Cookie
import play.api.test.FakeRequest
import play.api.test.Helpers._
import org.mockito.Matchers._

import scala.concurrent.{ExecutionContext, Future}

class RegisterActionSpec extends PlaySpec with MockitoSugar {

  trait WithControllerMockedDependencies {
    val mockIdentityService = mock[IdentityService]
    val messages = mock[MessagesApi]
    val controller = new RegisterAction(mockIdentityService, messages)
  }

  def fakeRegisterRequest(
                           firstName: String,
                           lastName: String,
                           email: String,
                           username: String,
                           password: String,
                           receiveGnmMarketing: Boolean,
                           receive3rdPartyMarketing: Boolean
                         ) = {
    val bodyParams = Seq(
      "firstName" -> firstName,
      "lastName" -> lastName,
      "email" -> email,
      "username" -> username,
      "password" -> password,
      "receiveGnmMarketing" -> receiveGnmMarketing.toString,
      "receive3rdPartyMarketing" -> receive3rdPartyMarketing.toString)

    FakeRequest("POST", "/actions/register").withFormUrlEncodedBody(bodyParams: _*)
  }

  "POST /register" should {
    "redirect to theguardian.com" in new WithControllerMockedDependencies {
      val firstName = "first"
      val lastName = "last"
      val email = "test@email.com"
      val username = "username"
      val password = "password"
      val receiveGnmMarketing = false
      val receive3rdPartyMarketing = false

      val testCookie = Cookie("SC_GU_U", "##hash##")

      when(mockIdentityService.register(anyObject(), anyString())(argAny[ExecutionContext]))
        .thenReturn{
          Future.successful {
           Right(Seq(testCookie))
          }
        }

      val result = call(controller.register, fakeRegisterRequest(firstName, lastName, email, username, password, receiveGnmMarketing, receive3rdPartyMarketing))
      val resultCookies = cookies(result)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustEqual Some("http://www.theguardian.com")

      resultCookies.size mustEqual 1
      resultCookies.head mustEqual testCookie
    }
  }
}
