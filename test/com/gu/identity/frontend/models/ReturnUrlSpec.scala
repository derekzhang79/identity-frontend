package com.gu.identity.frontend.models

import com.gu.identity.frontend.configuration.Configuration
import org.scalatest.{Matchers, FlatSpec}

class ReturnUrlSpec extends FlatSpec with Matchers {

  import ReturnUrl._

  val config = Configuration.testConfiguration

  it should "determine valid domain" in {

    validDomain(ReturnUrl("http://www.theguardian.com")) should be(true)
    validDomain(ReturnUrl("http://jobs.theguardian.com")) should be(true)
    validDomain(ReturnUrl("http://code.dev-theguardian.com")) should be(true)
    validDomain(ReturnUrl("http://thegulocal.com")) should be(true)
    validDomain(ReturnUrl("http://baddomain.com")) should be(false)
  }

  it should "construct return url" in {

    ReturnUrl(Some("http://www.theguardian.com/uk"), None, config) should be(ReturnUrl("http://www.theguardian.com/uk"))
    ReturnUrl(None, Some("http://www.theguardian.com/uk"), config) should be(ReturnUrl("http://www.theguardian.com/uk"))

    ReturnUrl(Some("http://jobs.theguardian.com/apply"), None, config) should be(ReturnUrl("http://jobs.theguardian.com/apply"))
    ReturnUrl(None, Some("http://jobs.theguardian.com/apply"), config) should be(ReturnUrl("http://jobs.theguardian.com/apply"))

    ReturnUrl(None, Some("http://www.thegulocal.com/"), config) should be(ReturnUrl("http://www.thegulocal.com/"))
    ReturnUrl(None, None, config) should be(ReturnUrl("http://www.theguardian.com"))

    ReturnUrl(Some("http://baddomain.com"), None, config) should be(ReturnUrl("http://www.theguardian.com"))
    ReturnUrl(None, Some("http://baddomain.com"), config) should be(ReturnUrl("http://www.theguardian.com"))

    ReturnUrl(None, Some("http://profile-origin2.thegulocal.com"), config) should be(ReturnUrl("http://profile-origin2.thegulocal.com"))
  }

  it should "Determine valid url path" in {
    validUrlPath(ReturnUrl("http://theguardian.com/signin")) should be(false)
    validUrlPath(ReturnUrl("http://theguardian.com/register")) should be(false)
    validUrlPath(ReturnUrl("http://theguardian.com/register/confirm")) should be(false)
    validUrlPath(ReturnUrl("http://theguardian.com")) should be(true)
    validUrlPath(ReturnUrl("http://theguardian.com/politics")) should be(true)
  }
  
  it should "Use the default return url" in {
    val codeConfig = config.copy(identityDefaultReturnUrl = "http://m.code.dev-theguardian.com")

    ReturnUrl(None, None, codeConfig) should be(ReturnUrl("http://m.code.dev-theguardian.com"))

    ReturnUrl(None, None, config) should be(ReturnUrl("http://www.theguardian.com"))
  }
}
