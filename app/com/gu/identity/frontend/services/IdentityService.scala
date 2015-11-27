package com.gu.identity.frontend.services

import javax.inject.Inject

import com.google.inject.ImplementedBy
import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.service.client._
import org.joda.time.{DateTime, Seconds}
import play.api.mvc.{Cookie => PlayCookie}

import scala.concurrent.{ExecutionContext, Future}


/**
 * Adapter for the identity service client.
 */
@ImplementedBy(classOf[IdentityServiceImpl])
trait IdentityService {
  def authenticate(email: Option[String], password: Option[String], rememberMe: Boolean)(implicit ec: ExecutionContext): Future[Either[Seq[ServiceError], Seq[PlayCookie]]]
}


class IdentityServiceImpl @Inject() (config: Configuration, httpProvider: IdentityServiceHttpProvider) extends IdentityService {

  implicit val clientConfiguration = IdentityClientConfiguration(config.identityApiHost, config.identityApiKey, httpProvider, IdentityServiceJsonParser)

  def authenticate(email: Option[String], password: Option[String], rememberMe: Boolean)(implicit ec: ExecutionContext) = {
    IdentityClient.authenticateCookies(email, password).map {
      case Left(errors) => Left {
        errors.map { e =>
          ServiceBadRequest(e.message, e.description)
        }
      }
      case Right(cookies) => Right(cookies.map { c =>
        val maxAge = if (rememberMe) Some(Seconds.secondsBetween(DateTime.now, c.expires).getSeconds) else None
        val secureHttpOnly = c.key.startsWith("SC_")
        val cookieMaxAgeOpt = maxAge.filterNot(_ => c.isSession)

        PlayCookie(c.key, c.value, cookieMaxAgeOpt, "/", Some("thegulocal.com"), secure = secureHttpOnly, httpOnly = secureHttpOnly)
      })
    }
  }

}
