package com.gu.identity.frontend.csrf

import com.gu.identity.frontend.test.SimpleAppPerSuite
import org.scalatestplus.play.PlaySpec
import play.api.mvc.Controller
import play.api.test._
import play.api.test.Helpers._

class CSRFActionsSpec extends PlaySpec with SimpleAppPerSuite {

  val csrfConfig = CSRFConfig()

  "Add token Action" must {

    val controller = new Controller {
      def testEndpoint = CSRFAddToken(csrfConfig) {
        Ok("test response")
      }
    }

    "have non-cacheable response" in {
      val resp = controller.testEndpoint(FakeRequest())

      val cacheHeader = header("Cache-Control", resp)

      cacheHeader.value must include("no-cache")
    }

  }
}
