package com.gu.identity.frontend.configuration

import play.api.{Configuration => PlayConfiguration}

trait Configuration {
  val identityCookieDomain: String

  val identityApiKey: String
  val identityApiHost: String

  val omnitureAccount: String

  val appConfiguration: PlayConfiguration
}

class ApplicationConfiguration(val appConfiguration: PlayConfiguration) extends Configuration {

  private def getString(path: String) =
    appConfiguration.getString(path).getOrElse(sys.error(s"Missing configuration: $path"))

  val identityCookieDomain = getString("identity.frontend.cookieDomain")

  val identityApiKey = getString("identity.api.key")
  val identityApiHost = getString("identity.api.host")

  val omnitureAccount = getString("omniture.account")
}
