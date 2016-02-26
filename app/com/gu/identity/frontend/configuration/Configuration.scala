package com.gu.identity.frontend.configuration

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth._
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions._
import play.api.{Configuration => PlayConfiguration}


case class Configuration(
  identityCookieDomain: String,

  identityApiKey: String,
  identityApiHost: String,

  identityProfileBaseUrl: String,

  identityDefaultReturnUrl: String,

  identityFederationApiHost: String,

  omnitureAccount: String,

  recaptchaEnabled: Boolean,
  googleRecaptchaSiteKey: String,
  googleRecaptchaSecretKey: String,

  identityCookiePublicKey: String,

  underlying: PlayConfiguration)


object Configuration {
  def apply(appConfiguration: PlayConfiguration): Configuration = {

    def getString(path: String) =
      appConfiguration.getString(path).getOrElse(sys.error(s"Missing configuration: $path"))

    def getBoolean(path: String) =
      appConfiguration.getBoolean(path).getOrElse(sys.error(s"Missing configuration: $path"))


    Configuration(
      identityCookieDomain = getString("identity.frontend.cookieDomain"),

      identityApiKey = getString("identity.api.key"),
      identityApiHost = getString("identity.api.host"),

      identityFederationApiHost = getString("identity.federation.api.host"),

      identityProfileBaseUrl = getString("identity.frontend.baseUrl"),

      identityDefaultReturnUrl = getString("identity.frontend.defaultReturnUrl"),

      omnitureAccount = getString("omniture.account"),

      googleRecaptchaSiteKey = getString("google.recaptcha.site"),

      googleRecaptchaSecretKey = getString("google.recaptcha.secret"),

      recaptchaEnabled = getBoolean("google.recaptchaEnabled"),

      identityCookiePublicKey = getString("identityCookie.publicKey"),

      underlying = appConfiguration
    )
  }


  lazy val testConfiguration = Configuration(
    identityCookieDomain = "dev-theguardian.com",

    identityApiKey = "--test-key--",
    identityApiHost = "idapi.code.dev-theguardian.com",

    identityFederationApiHost = "https://oauth.code.dev-theguardian.com",

    identityProfileBaseUrl = "https://profile.code.dev-theguardian.com",

    identityDefaultReturnUrl = "http://www.theguardian.com",

    omnitureAccount = "--test-omniture-account--",

    googleRecaptchaSiteKey = "--recaptcha-key--",

    googleRecaptchaSecretKey = "--recaptcha-secret-key--",

    recaptchaEnabled = true,

    identityCookiePublicKey = "MIIDOjCCAi0GByqGSM44BAEwggIgAoIBAQDLHjbLapKix+5Qlo3cZ6qlSXuQIG4sZxfSkD70UtdBYgXcdZa2jaNz4G/bOn+Mn+KN1KTlIupDr9+et9xy902G1JRxqAzzKgelLhEh5nTNYbOVjdi8Odg4yLqwpljTf0javSCxUE1wuOsWOZ4BNZwBpxcXeUd0MBshhLXtZiILHHLAq/4mkYBH9V/dHQXUDf7+X+kXNtR4KglvoH3zYVQ1thxUQ60rcSpkgtybHnibd5x4dVtFok4IMV05N0qW1tAhZIFWX8Dx2xene6QmEMPKsc2gJBsxWlGkX7YJiH0IMkK1D1EyzQ48TNP7oPBHeHeKtmWhFGYxsGC1XCxO3g2rAhUA8qS04q28iMQrTqgfxIZURoj5gd0CggEAUbBAySBWAY+mldTquKYHzzXylp8ocw+mpOaA+cyDvd4mucQUw/3REsZw6QlbQcGzhck2WXvJSzStAj/6PglJLMtvE/xoAk24w95iwRdaN+fYiqL0uZUc1aDXKh6BbYV1u9nlHrlv5ZfMC/dWVJCOOwYjcTfWDyTxquFDp0hT6V7sMer5Ky/xPnbAXmB/1dDomeRhKOGzf9S+aLQZR5ePV6NPLF/eh+Zb/YuHrBHI4evC2sGm6gnllyAmFdRtlT6D4/f7N7wiyBQCSpH4/XF9vGSWfRU5Wp2FgHufQd8HOXDtVnGm7KUR8A6OwHllM8zSJhI1dJtv0oD77Px0Ehea8gOCAQUAAoIBABcDIX6rJFhVPLgGrBlkZcTHqj/5Og1gRSFYSN0B7Ox3M0q3NpCaMUvrbuY8hy77M+ppHEwM8DvQ6lmMCYxUNiqTi6VGgCimZJckgWISFHXs5wqLTL5xAd8Ebohils8kljwIxV/cf+yMz9jVI+zZ8MWG5aIxDy9ThYsPIOqxpIm9tNrLyHq2LUZhM80HGSZHM2bpImg46E3OnfjV/f8mbxIEjLSaCi4kqLfSm1fiF5QzU9tnkmtxCntKvAgirOD5uWUoXsAwDktg2G5lJu9Bhp/hbzvkklp+HUBeJF+5VqnnpccYfZZT7nJ7KNNYPybGFYW6NxgTX7zptGH1chQPGvg=",

    underlying = PlayConfiguration.empty
  )

  object AWSConfig {
    val credentials: AWSCredentialsProvider = {
      val provider = new AWSCredentialsProviderChain(
        new EnvironmentVariableCredentialsProvider(),
        new SystemPropertiesCredentialsProvider(),
        new ProfileCredentialsProvider(),
        new InstanceProfileCredentialsProvider
      )
      provider
    }

    val clientConfiguration: ClientConfiguration = new ClientConfiguration()

    val region: Region = {
      val defaultRegion: Region = Region.getRegion(EU_WEST_1)
      Option(getCurrentRegion()).getOrElse(defaultRegion)
    }

  }

  object Environment {
    lazy val stage = System.getProperty("stage", "DEV")
  }
}
