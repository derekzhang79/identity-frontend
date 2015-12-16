package com.gu.identity.service.client

import com.gu.identity.frontend.models.TrackingData
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.mock.MockitoSugar


class AuthenticateCookiesRequestSpec extends WordSpec with Matchers with MockitoSugar {

  val handler = mock[IdentityClientRequestHandler]

  implicit val testConfig = IdentityClientConfiguration("test.gu.com", "##key##", handler)

  val trackingData = TrackingData(
    returnUrl = Some("https://profile.theguardian.com"),
    registrationType = Some("facebook"),
    omnitureSVi = Some("omnitureCode"),
    ipAddress = Some("127.0.0.1"),
    referrer = Some("http://www.theguardian.com"),
    userAgent = Some("chrome")
  )

  "Authenticate Cookies API Request" should {
    "Parse correctly with a valid email address, password" in {
      val email = Some("test@guardian.co.uk")
      val password = Some("god")

      val result = AuthenticateCookiesRequest.from(email, password, rememberMe = false, trackingData)

      result.isRight shouldBe true
      result.right.get.email should equal (email.get)
      result.right.get.password should equal(password.get)
      result.right.get.parameters should contain("persistent" -> "false")
    }

    "Parse correctly with a valid email address and password that requires encoded params" in {
      val email = Some("test@guardian.co.uk")
      val password = Some("some%thing")

      val result = AuthenticateCookiesRequest.from(email, password, rememberMe = false, trackingData)

      result.isRight shouldBe true
      result.right.get.email should equal (email.get)
      result.right.get.password should equal(password.get)
      result.right.get.parameters should contain("persistent" -> "false")
      result.right.get.body should equal(Some("email=test%40guardian.co.uk&password=some%25thing"))
    }

    "Set persistent parameter on request" in {
      val email = Some("test@guardian.co.uk")
      val password = Some("god")

      val result = AuthenticateCookiesRequest.from(email, password, rememberMe = true, trackingData)

      result.isRight shouldBe true
      result.right.get.parameters should contain("persistent" -> "true")
    }

    "Set tracking data parameters on request" in {
      val email = Some("test@guardian.co.uk")
      val password = Some("god")

      val result = AuthenticateCookiesRequest.from(email, password, rememberMe = true, trackingData)

      result.isRight shouldBe true
      val params = result.right.get.parameters
      trackingData.parameters.foreach( td => params should contain(td))
    }

    "Fail when given an invalid email domain input" in {
      val email = Some("me@")
      val password = Some("god")

      val result = AuthenticateCookiesRequest.from(email, password, rememberMe = false, trackingData)

      result.isLeft shouldBe true
      result.left.get shouldBe a[BadRequest]
    }


    "Fail when given an invalid start of email input" in {
      val email = Some("@blah.com")
      val password = Some("god")

      val result = AuthenticateCookiesRequest.from(email, password, rememberMe = false, trackingData)

      result.isLeft shouldBe true
      result.left.get shouldBe a[BadRequest]
    }

    "Fail when given an empty email input" in {
      val email = Some("")
      val password = Some("god")

      val result = AuthenticateCookiesRequest.from(email, password, rememberMe = false, trackingData)

      result.isLeft shouldBe true
      result.left.get shouldBe a[BadRequest]
    }

    "Fail when given an invalid password input" in {
      val email = Some("me@guardian.co.uk")
      val password = Some("")

      val result = AuthenticateCookiesRequest.from(email, password, rememberMe = false, trackingData)

      result.isLeft shouldBe true
      result.left.get shouldBe a[BadRequest]
    }
  }

}
