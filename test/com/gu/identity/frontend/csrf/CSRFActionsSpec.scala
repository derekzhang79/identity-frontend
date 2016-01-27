package com.gu.identity.frontend.csrf

import com.gu.identity.frontend.test.SimpleAppPerSuite
import org.scalatestplus.play.PlaySpec
import play.api.mvc.Controller
import play.api.test._
import play.api.test.Helpers._
import play.filters.csrf.{CSRFConfig => PlayCSRFConfig}

class CSRFActionsSpec extends PlaySpec with SimpleAppPerSuite {

  val csrfCookieName = "GU_TEST_CSRF"
  val underlyingCsrfConfig = PlayCSRFConfig(
    cookieName = Some(csrfCookieName)
  )
  val csrfConfig = CSRFConfig(underlyingCsrfConfig)

  val controller = new Controller {
    def testCSRFAddToken = CSRFAddToken(csrfConfig) {
      Ok("test add token response")
    }

    def testCSRFCheck = CSRFCheck(csrfConfig) {
      Ok("test check response")
    }
  }

  "Add token Action" must {

    "have non-cacheable response" in {
      val resp = controller.testCSRFAddToken(FakeRequest())

      val cacheHeader = header("Cache-Control", resp)

      cacheHeader.value must include("no-cache")
    }

    "have token on response" in {
      val resp = controller.testCSRFAddToken(FakeRequest())

      val respCookies = cookies(resp)
      val csrfCookie = respCookies.get(csrfCookieName)

      csrfCookie must not be empty
    }

  }


  "Check token Action" must {

    "have non-cacheable response" in {
      val resp = controller.testCSRFCheck(FakeRequest())

      val cacheHeader = header("Cache-Control", resp)

      cacheHeader.value must include("no-cache")
    }

  }
}
