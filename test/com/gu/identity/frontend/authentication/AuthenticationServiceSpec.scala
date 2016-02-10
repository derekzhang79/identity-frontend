package com.gu.identity.frontend.authentication

import com.gu.identity.cookie.{IdentityKeys, IdentityCookieDecoder}
import com.gu.identity.frontend.configuration.Configuration
import org.scalatestplus.play.PlaySpec
import play.api.mvc.Cookie
import play.api.test.FakeRequest

class AuthenticationServiceSpec extends PlaySpec {

  val testKeys = new IdentityKeys(Configuration.testConfiguration.identityCookiePublicKey)

  val cookieDecoder = new IdentityCookieDecoder(testKeys)

  val autheticationService = new AuthenticationService(cookieDecoder)



  def requestWithCookie(value: String, name: String = "SC_GU_U") = {
    val cookie = Cookie(name = name, value = value)
    FakeRequest().withCookies(cookie)
  }

  "AuthenticatedUserFor" should {

    "decode valid cookie" in {
      val validRequestCookieData = "WyIxMDAwMDgxMSIsMTQ2Mjg5MjgyNDYxMV0.MCwCFG_PdoPk2PpSO5KoXbRLWJ0BvuqhAhRFIt1mlDcO2SN1Y6X7ktSs_oRJJw"
      val validCookieRequest = requestWithCookie(value = validRequestCookieData)
      val response = autheticationService.authenticatedUserFor(validCookieRequest)
      val expectedUser = AuthenticatedUser("10000811")

      response mustEqual Some(expectedUser)
    }

    "fail to decode invalid cookie" in {
      val invalidCookieRequest  = requestWithCookie(value = "abc")
      val response = autheticationService.authenticatedUserFor(invalidCookieRequest)

      response mustEqual None
    }
  }
}
