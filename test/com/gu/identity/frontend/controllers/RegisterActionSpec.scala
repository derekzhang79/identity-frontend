package com.gu.identity.frontend.controllers

import com.gu.identity.frontend.models.TrackingData
import com.gu.identity.frontend.services.IdentityService
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.MessagesApi
import play.api.mvc.Cookie
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.{Future, ExecutionContext}

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
                           ): Unit = {
    val bodyParams = Seq(
      "firstName" -> firstName,
      "lastName" -> lastName,
      "email" -> email,
      "username" -> username,
      "password" -> password,
      "receiveGnmMarketing" -> receiveGnmMarketing.toString,
      "receive3rdPartyMarketing" -> receive3rdPartyMarketing.toString).map(p => p._1 -> p._2)

    FakeRequest("POST", "/actions/register")
      .withFormUrlEncodedBody(bodyParams: _*)

  }

  "POST /register" should {
    "redirect to theguardian.com" in new WithControllerMockedDependencies {
      val registerRequest = RegisterRequest("first", "last", "me@me.com", "username", "password", false, false)
      true mustEqual true
      //TODO
    }
  }
}
