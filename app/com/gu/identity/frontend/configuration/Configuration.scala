package com.gu.identity.frontend.configuration

import com.google.inject.ImplementedBy
import play.api.Play.current


@ImplementedBy(classOf[ApplicationConfiguration])
trait Configuration {
  val identityCookieDomain: String

  val identityApiKey: String
  val identityApiHost: String
}

class ApplicationConfiguration extends Configuration {

  private def getString(path: String) =
    current.configuration.getString(path).getOrElse(sys.error(s"Missing configuration: $path"))

  val identityCookieDomain = getString("identity.frontend.cookieDomain")

  val identityApiKey = getString("identity.api.key")
  val identityApiHost = getString("identity.api.host")
}
