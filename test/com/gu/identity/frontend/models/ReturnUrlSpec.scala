package com.gu.identity.frontend.models

import java.net.URI

import com.gu.identity.frontend.configuration.Configuration
import org.scalatest.{Matchers, FlatSpec}

class ReturnUrlSpec extends FlatSpec with Matchers {

  import ReturnUrl._

  val config = Configuration.testConfiguration

  it should "determine valid domain" in {

    validDomain(new URI("http://www.theguardian.com")) should be(true)
    validDomain(new URI("http://jobs.theguardian.com")) should be(true)
    validDomain(new URI("http://code.dev-theguardian.com")) should be(true)
    validDomain(new URI("http://thegulocal.com")) should be(true)
    validDomain(new URI("http://baddomain.com")) should be(false)
  }

  it should "construct return url" in {

    ReturnUrl(Some("http://www.theguardian.com/uk"), None, config) should be(ReturnUrl(new URI("http://www.theguardian.com/uk")))
    ReturnUrl(None, Some("http://www.theguardian.com/uk"), config) should be(ReturnUrl(new URI("http://www.theguardian.com/uk")))

    ReturnUrl(Some("http://jobs.theguardian.com/apply"), None, config) should be(ReturnUrl(new URI("http://jobs.theguardian.com/apply")))
    ReturnUrl(None, Some("http://jobs.theguardian.com/apply"), config) should be(ReturnUrl(new URI("http://jobs.theguardian.com/apply")))

    ReturnUrl(None, Some("http://www.thegulocal.com/"), config) should be(ReturnUrl(new URI("http://www.thegulocal.com/")))

    ReturnUrl(None, Some("http://profile-origin2.thegulocal.com"), config) should be(ReturnUrl(new URI("http://profile-origin2.thegulocal.com")))
  }


  it should "Determine valid url path" in {
    validUrlPath(new URI("http://theguardian.com/signin")) should be(false)
    validUrlPath(new URI("http://theguardian.com/register")) should be(false)
    validUrlPath(new URI("http://theguardian.com/register/confirm")) should be(false)
    validUrlPath(new URI("http://theguardian.com")) should be(true)
    validUrlPath(new URI("http://theguardian.com/politics")) should be(true)
  }


  it should "Retrieve default Return URL" in {
    def assertDefaultFallbackFor(in: ReturnUrl) = {
      in.url should be(config.identityDefaultReturnUrl)
      in shouldBe 'default
    }

    assertDefaultFallbackFor(ReturnUrl(None, None, config))
    assertDefaultFallbackFor(ReturnUrl(Some("http://baddomain.com"), None, config))
    assertDefaultFallbackFor(ReturnUrl(None, Some("http://badreferrer.com"), config))
    assertDefaultFallbackFor(ReturnUrl(Some("^!this-is\\a-bad-uri"), None, config))
    assertDefaultFallbackFor(ReturnUrl(None, Some("^!this-is\\a-bad-referer!"), config))
  }


  it should "Use the default return url for CODE env" in {
    val codeConfig = config.copy(identityDefaultReturnUrl = "http://m.code.dev-theguardian.com")

    ReturnUrl(None, None, codeConfig) should be(ReturnUrl(new URI("http://m.code.dev-theguardian.com"), isDefault = true))

    ReturnUrl(None, None, config) should be(ReturnUrl(new URI("http://www.theguardian.com"), isDefault = true))
  }
}
