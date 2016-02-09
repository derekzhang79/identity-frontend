package com.gu.identity.frontend.authentication

import com.gu.identity.cookie.{PreProductionKeys, IdentityCookieDecoder}
import org.scalatestplus.play.PlaySpec
import play.api.mvc.Cookie
import play.api.test.FakeRequest

class AuthenticationServiceTest extends PlaySpec {

  val cookieDecoder = new IdentityCookieDecoder(new PreProductionKeys())

  val autheticationService = new AuthenticationService(cookieDecoder)

  def requestWithCookie(value: String, name: String = "SC_GU_U") = {
    val cookie = Cookie(name = name, value = value)
    FakeRequest().withCookies(cookie)
  }

  "AuthenticatedUserFor" should {

    "decode valid cookie" in {
      val validRequestCookieData = "WyIyMTgzOTIyMSIsMTQ2MjgxNjE4NTg3NV0.MCwCFC8sibZouZgjsEKqE8EFJX81_2g1AhRziWMAzxc9oNmN0SYlHG6vVs1PgA"
      val validCookieRequest = requestWithCookie(value = validRequestCookieData)
      val response = autheticationService.authenticatedUserFor(validCookieRequest)
      val expectedUser = AuthenticatedUser("21839221")

      response mustEqual Some(expectedUser)
    }

    "fail to decode invalid cookie" in {
      val invalidCookieRequest  = requestWithCookie(value = "abc")
      val response = autheticationService.authenticatedUserFor(invalidCookieRequest)

      response mustEqual None
    }
  }
}
