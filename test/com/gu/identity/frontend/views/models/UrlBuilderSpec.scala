package com.gu.identity.frontend.views.models

import org.scalatest.{FlatSpec, Matchers}

class UrlBuilderSpec extends FlatSpec with Matchers{

  it should "create url register with return url" in {
    UrlBuilder("register", Seq(("returnUrl", "www.test.com"))) should be ("register?returnUrl=www.test.com")
  }

  it should "create url register with no params" in {
    UrlBuilder("register", Seq.empty) should be ("register")
  }

  it should "create a register url with 'returnUrl=www.test.com' and ignore any None objects" in {
    UrlBuilder("register", Seq(("returnUrl", "www.test.com"))) should be ("register?returnUrl=www.test.com")
  }

  it should "create a url with return url params encoded" in {
    val params = Seq(
      "returnUrl" -> "https://subscribe.theguardian.com/?INTCMP=NGW_HEADER_UK_GU_SUBSCRIBE&yes=no",
      "another" -> "hello"
    )

    UrlBuilder("/register", params) should be (
      "/register?returnUrl=https%3A%2F%2Fsubscribe.theguardian.com%2F%3FINTCMP%3DNGW_HEADER_UK_GU_SUBSCRIBE%26yes%3Dno&another=hello"
    )
  }

}
