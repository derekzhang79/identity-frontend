package com.gu.identity.frontend.controllers

import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.mvc.{Action, Controller}
import play.api.test.FakeRequest
import play.api.test.Helpers._

class CachedSpec extends PlaySpec with MockitoSugar {

  class TestController extends Controller {
    def cachedEndpoint = Action {
      Cached {
        Ok("Ok")
      }
    }

    def notCachedEndpoint = Action {
      NoCache {
        Ok("Ok")
      }
    }
  }

  val controller = new TestController

  "Cached" should {
    "Add Cache-Control header to response" in {
      val resp = controller.cachedEndpoint(FakeRequest())
      val headerValue = header("Cache-Control", resp)

      headerValue must equal(Some("public, max-age=60, stale-while-revalidate=6, stale-if-error=864000"))
    }

    "Add Expires header to response" in {
      val resp = controller.cachedEndpoint(FakeRequest())
      val headerValue = header("Expires", resp)

      headerValue must not be None
    }

    "Add Date header to response" in {
      val resp = controller.cachedEndpoint(FakeRequest())
      val headerValue = header("Expires", resp)

      headerValue must not be None
    }
  }

  "NoCache" should {
    "Add Cache-Control header to response" in {
      val resp = controller.notCachedEndpoint(FakeRequest())
      val headerValue = header("Cache-Control", resp)

      headerValue must equal(Some("no-cache, private"))
    }

    "Add Pragma header to response" in {
      val resp = controller.notCachedEndpoint(FakeRequest())
      val headerValue = header("Pragma", resp)

      headerValue must equal(Some("no-cache"))
    }
  }

}
