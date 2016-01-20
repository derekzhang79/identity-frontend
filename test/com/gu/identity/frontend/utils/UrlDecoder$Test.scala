package com.gu.identity.frontend.utils

import org.scalatestplus.play.PlaySpec

class UrlDecoder$Test extends PlaySpec {

  "getQueryParams" should {
    "return a map containing a return url" in {
      val queryParams = UrlDecoder.getQueryParams("/register?returnUrl=http://www.theguardian.com/test")

      queryParams.contains("returnUrl") mustEqual true
      queryParams.get("returnUrl") mustEqual Some("http://www.theguardian.com/test")
    }

    "return a map containing a return url and an error" in {
      val queryParams = UrlDecoder.getQueryParams("/register?error=error-bad-gateway&returnUrl=http://www.theguardian.com/test")

      queryParams.contains("returnUrl") mustEqual true
      queryParams.get("returnUrl") mustEqual Some("http://www.theguardian.com/test")
      queryParams.contains("error") mustEqual true
      queryParams.get("error") mustEqual Some("error-bad-gateway")
    }

    "return an empty map when there are no params" in {
      val queryParams = UrlDecoder.getQueryParams("/register")

      queryParams.isEmpty mustEqual true
    }

    "ignore params than do not have an equals sign" in {
        val queryParams = UrlDecoder.getQueryParams("/register?returnUrlhttp://www.theguardian.com/test")

        queryParams.isEmpty mustEqual true
    }
  }
}
