package com.gu.identity.frontend.configuration

import com.google.inject.ImplementedBy
import play.api.Play.current
import play.api.{Configuration => PlayConfiguration}


@ImplementedBy(classOf[ApplicationConfiguration])
trait Configuration {
  val appConfiguration: PlayConfiguration

  val identityCookieDomain: String

  val identityApiKey: String
  val identityApiHost: String

  val omnitureAccount: String
}

class ApplicationConfiguration extends Configuration {
  val appConfiguration = current.configuration

  private def getString(path: String) =
    appConfiguration.getString(path).getOrElse(sys.error(s"Missing configuration: $path"))

  val identityCookieDomain = getString("identity.frontend.cookieDomain")

  val identityApiKey = getString("identity.api.key")
  val identityApiHost = getString("identity.api.host")

  val omnitureAccount = getString("omniture.account")
}
