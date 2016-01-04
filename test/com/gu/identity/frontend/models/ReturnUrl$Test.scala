package com.gu.identity.frontend.models

import org.scalatest.{Matchers, FlatSpec}

class ReturnUrl$Test extends FlatSpec with Matchers {

  import ReturnUrl._

  it should "determine valid domain" in {

    validDomain(ReturnUrl("http://www.theguardian.com")) should be(true)
    validDomain(ReturnUrl("http://jobs.theguardian.com")) should be(true)
    validDomain(ReturnUrl("http://code.dev-theguardian.com")) should be(true)
    validDomain(ReturnUrl("http://thegulocal.com")) should be(true)
    validDomain(ReturnUrl("http://baddomain.com")) should be(false)
  }

  it should "construct return url" in {

    ReturnUrl(Some("http://www.theguardian.com/uk"), None) should be(ReturnUrl("http://www.theguardian.com/uk"))
    ReturnUrl(None, Some("http://www.theguardian.com/uk")) should be(ReturnUrl("http://www.theguardian.com/uk"))

    ReturnUrl(Some("http://jobs.theguardian.com/apply"), None) should be(ReturnUrl("http://jobs.theguardian.com/apply"))
    ReturnUrl(None, Some("http://jobs.theguardian.com/apply")) should be(ReturnUrl("http://jobs.theguardian.com/apply"))

    ReturnUrl(None, Some("http://www.thegulocal.com/")) should be(ReturnUrl("http://www.thegulocal.com/"))
    ReturnUrl(None, None) should be(ReturnUrl("http://www.theguardian.com"))

    ReturnUrl(Some("http://baddomain.com"), None) should be(ReturnUrl("http://www.theguardian.com"))
    ReturnUrl(None, Some("http://baddomain.com")) should be(ReturnUrl("http://www.theguardian.com"))

    ReturnUrl(None, Some("http://profile-origin2.thegulocal.com")) should be(ReturnUrl("http://profile-origin2.thegulocal.com"))
  }

}
