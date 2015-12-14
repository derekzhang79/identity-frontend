package com.gu.identity.frontend.filters

import javax.inject.{Singleton, Inject}

import com.gu.identity.frontend.configuration.Configuration
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Cookie, Filter, RequestHeader, Result}

import scala.concurrent.Future

@Singleton
class BetaUserGroupFilter @Inject() (configuration: Configuration)  extends Filter {

  private[filters] val CookieName = "GU_PROFILE_BETA"
  private[filters] val CookieValue = "1"

  override def apply(nextFilter: (RequestHeader) => Future[Result])(request: RequestHeader): Future[Result] = {
    val cookie = Cookie(
      name = CookieName,
      value = CookieValue,
      domain = Some(configuration.identityCookieDomain),
      maxAge = None
    )
    nextFilter(request).map(_.withCookies(cookie))
  }
}
