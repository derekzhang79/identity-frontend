package com.gu.identity.frontend.configuration

import play.api.{Configuration => PlayConfiguration}

trait Configuration {
  val identityCookieDomain: String

  val identityApiKey: String
  val identityApiHost: String

  val identityProfileBaseUrl: String

  val identityFederationApiHost: String

  val omnitureAccount: String

  val recaptchaEnabled: Boolean

  val googleRecaptchaSiteKey: String

  val googleRecaptchaSecretKey: String

  val appConfiguration: PlayConfiguration
}

class ApplicationConfiguration(val appConfiguration: PlayConfiguration) extends Configuration {

  private def getString(path: String) =
    appConfiguration.getString(path).getOrElse(sys.error(s"Missing configuration: $path"))

  private def getBoolean(path: String) =
    appConfiguration.getBoolean(path).getOrElse(sys.error(s"Missing configuration: $path"))

  val identityCookieDomain = getString("identity.frontend.cookieDomain")

  val identityApiKey = getString("identity.api.key")
  val identityApiHost = getString("identity.api.host")

  val identityFederationApiHost = getString("identity.federation.api.host")

  val identityProfileBaseUrl = getString("identity.frontend.baseUrl")

  val omnitureAccount = getString("omniture.account")

  val googleRecaptchaSiteKey = getString("google.recaptcha.site")

  val googleRecaptchaSecretKey = getString("google.recaptcha.secret")

  val recaptchaEnabled = getBoolean("google.recaptchaEnabled")
}
