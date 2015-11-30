package com.gu.identity.service.client

import org.scalatest.{Matchers, WordSpec}
import org.scalatest.mock.MockitoSugar


class AuthenticateCookiesRequestSpec extends WordSpec with Matchers with MockitoSugar {

  "Authenticate Cookies API Request" should {
    "Parse correctly with a valid email address and password" in {
      val email = Some("test@guardian.co.uk")
      val password = Some("god")

      val result = AuthenticateCookiesRequest.from(email, password)

      result.isRight shouldBe true
      result.right.get.email should equal (email.get)
      result.right.get.password should equal(password.get)
    }

    "Fail when given an invalid email domain input" in {
      val email = Some("me@")
      val password = Some("god")

      val result = AuthenticateCookiesRequest.from(email, password)

      result.isLeft shouldBe true
      result.left.get shouldBe a[BadRequest]
    }


    "Fail when given an invalid start of email input" in {
      val email = Some("@blah.com")
      val password = Some("god")

      val result = AuthenticateCookiesRequest.from(email, password)

      result.isLeft shouldBe true
      result.left.get shouldBe a[BadRequest]
    }

    "Fail when given an empty email input" in {
      val email = Some("")
      val password = Some("god")

      val result = AuthenticateCookiesRequest.from(email, password)

      result.isLeft shouldBe true
      result.left.get shouldBe a[BadRequest]
    }

    "Fail when given an invalid password input" in {
      val email = Some("me@guardian.co.uk")
      val password = Some("")

      val result = AuthenticateCookiesRequest.from(email, password)

      result.isLeft shouldBe true
      result.left.get shouldBe a[BadRequest]
    }
  }

}
