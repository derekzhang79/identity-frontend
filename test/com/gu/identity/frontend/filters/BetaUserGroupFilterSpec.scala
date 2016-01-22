package com.gu.identity.frontend.filters

import com.gu.identity.frontend.configuration.Configuration
import org.scalatest.{WordSpec, Matchers}
import play.api.mvc.{Result, RequestHeader}
import play.api.test.FakeRequest

import scala.concurrent.Future
import play.api.mvc.Results._
import play.api.test.Helpers._
import play.api.{Configuration => PlayConfiguration}

class BetaUserGroupFilterSpec extends WordSpec with Matchers {

  val config = new Configuration {
    override val identityApiHost: String = "identityApiHost"
    override val identityApiKey: String = "identityApiKey"
    override val identityCookieDomain: String = "theguardian.com"
    override val identityProfileBaseUrl: String = "profile.theguardian.com"
    override val omnitureAccount: String = "omnitureAccount"
    override val appConfiguration: PlayConfiguration = null
    override val identityFederationApiHost: String = "https://oauth.theguardian.com"
  }

  val filter = new BetaUserGroupFilter(config)

  def action(req: RequestHeader): Future[Result] = Future.successful(Ok("ok"))

  "Beta User Group Filter" should {
    "Add a session cookie to every request" in {
      val req = FakeRequest()
      val resp = filter.apply(action _)(req)

      val cookie = cookies(resp).get(filter.CookieName)
      cookie should not be None
      cookie.map(_.value) shouldEqual Some(filter.CookieValue)
      cookie.flatMap(_.domain) shouldEqual Some(config.identityCookieDomain)
      cookie.flatMap(_.maxAge) shouldBe None
    }
  }
}
