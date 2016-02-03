package com.gu.identity.frontend.configuration

import play.api.{Configuration => PlayConfiguration}


case class Configuration(
  identityCookieDomain: String,

  identityApiKey: String,
  identityApiHost: String,

  identityProfileBaseUrl: String,

  identityFederationApiHost: String,

  omnitureAccount: String,

  recaptchaEnabled: Boolean,
  googleRecaptchaSiteKey: String,
  googleRecaptchaSecretKey: String,

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

      omnitureAccount = getString("omniture.account"),

      googleRecaptchaSiteKey = getString("google.recaptcha.site"),

      googleRecaptchaSecretKey = getString("google.recaptcha.secret"),

      recaptchaEnabled = getBoolean("google.recaptchaEnabled"),

      underlying = appConfiguration
    )
  }


  lazy val testConfiguration = Configuration(
    identityCookieDomain = "dev-theguardian.com",

    identityApiKey = "--test-key--",
    identityApiHost = "idapi.code.dev-theguardian.com",

    identityFederationApiHost = "https://oauth.code.dev-theguardian.com",

    identityProfileBaseUrl = "https://profile.code.dev-theguardian.com",

    omnitureAccount = "--test-omniture-account--",

    googleRecaptchaSiteKey = "--recaptcha-key--",

    googleRecaptchaSecretKey = "--recaptcha-secret-key--",

    recaptchaEnabled = true,

    underlying = PlayConfiguration.empty
  )

}
