package com.gu.identity.frontend.views.models

import org.scalatest.{FlatSpec, Matchers}

class UrlBuilder$Test extends FlatSpec with Matchers{

  it should "create url register with return url" in {
    UrlBuilder("register", Seq(Some("returnUrl", "www.test.com"))) should be ("register/?returnUrl=www.test.com")
  }

  it should "create url register with no params" in {
    UrlBuilder("register", Seq(None)) should be ("register")
  }

  it should "create a register url with 'returnUrl=www.test.com' and ignore any None objects" in {
    UrlBuilder("register", Seq(Some("returnUrl", "www.test.com"), None)) should be ("register/?returnUrl=www.test.com")
  }
}
