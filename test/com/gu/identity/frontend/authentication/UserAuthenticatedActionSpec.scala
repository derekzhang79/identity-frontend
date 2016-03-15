package com.gu.identity.frontend.authentication

import java.net.URI

import com.gu.identity.cookie.{IdentityKeys, IdentityCookieDecoder}
import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.models.GroupCode
import com.gu.identity.frontend.test.SimpleFakeApplication
import com.gu.identity.model.User
import org.scalatest.mock.MockitoSugar
import play.api.mvc.Cookie
import play.api.test.FakeRequest
import org.scalatestplus.play.PlaySpec
import play.api.mvc.Results.Ok
import play.api.test.Helpers._
import UserAuthenticatedActionBuilder.UserAuthenticatedAction

class UserAuthenticatedActionSpec extends PlaySpec with MockitoSugar {

  implicit val app = SimpleFakeApplication()
  val testKeys = new IdentityKeys(Configuration.testConfiguration.identityCookiePublicKey)


  "AuthenticatedUserAction" should {
    "stop a request if the cookie is absent or invalid" in {
      val cookieDecoder = new IdentityCookieDecoder(testKeys) {
        override def getUserDataForScGuU(cookieValue: String): Option[User] = None
      }

      running(app) {
        val action = UserAuthenticatedAction(cookieDecoder) {
          request => Ok
        }

        val request = FakeRequest("GET", "/agree/GRS")

        val result = call(action, request)

        status(result) mustEqual 303
        redirectLocation(result) mustEqual Some("/signin?group=GRS")
      }
    }

    "add a SC_GU_U cookie to the request if the a SC_GU_U cookie is valid" in {
      val cookieDecoder = new IdentityCookieDecoder(testKeys) {
        override def getUserDataForScGuU(cookieValue: String): Option[User] = Some(User(id = "1234"))
      }

      running(app) {
        val action = UserAuthenticatedAction(cookieDecoder) {
          request => {
            val scGuUCookie = request.scGuUCookie
            val cookieName = scGuUCookie.name
            val cookieValue = scGuUCookie.value
            Ok(s"$cookieName: $cookieValue")
          }
        }

        val scGuUCookie = Cookie("SC_GU_U", "abc")
        val request = FakeRequest("GET", "/agree/GRS").withCookies(scGuUCookie)

        val result = call(action, request)

        status(result) mustEqual 200
        contentAsString(result) mustEqual "SC_GU_U: abc"
      }
    }

    "redirect to signin if the cookie is present but invalid" in {
      val cookieDecoder = new IdentityCookieDecoder(testKeys) {
        override def getUserDataForScGuU(cookieValue: String): Option[User] = None
      }

      running(app) {
        val action = UserAuthenticatedAction(cookieDecoder) {
          request => {
            Ok
          }
        }

        val scGuUCookie = Cookie("SC_GU_U", "abc")
        val request = FakeRequest("GET", "/agree/GRS").withCookies(scGuUCookie)

        val result = call(action, request)

        status(result) mustEqual 303
      }
    }
  }

  "extractGroupCodeFromURI" should {
    "return a group code object when the uri contains a valid group code" in {
      val uri = new URI("/agree/GRS")
      val result = UserAuthenticatedActionBuilder.extractGroupCodeFromURI(uri)
      result mustEqual GroupCode("GRS")
    }

    "return None when the uri does not contain a valid group code" in {
      val uri = new URI("/agree/ABC")
      val result = UserAuthenticatedActionBuilder.extractGroupCodeFromURI(uri)
      result mustEqual None
    }

    "return None when the uri is empty" in {
      val uri = new URI("/")
      val result = UserAuthenticatedActionBuilder.extractGroupCodeFromURI(uri)
      result mustEqual None
    }
  }
}
