package com.gu.identity.frontend.services

import javax.inject.Inject

import com.google.inject.ImplementedBy
import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.service.client._
import play.api.mvc.{Cookie => PlayCookie}

import scala.concurrent.{ExecutionContext, Future}


/**
 * Adapter for the identity service client.
 */
@ImplementedBy(classOf[IdentityServiceImpl])
trait IdentityService {
  def authenticate(email: Option[String], password: Option[String])(implicit ec: ExecutionContext): Future[Either[ServiceError, Seq[PlayCookie]]]
}


class IdentityServiceImpl @Inject() (config: Configuration, httpProvider: IdentityServiceHttpProvider) extends IdentityService {

  implicit val clientConfiguration = IdentityClientConfiguration(config.identityApiHost, config.identityApiKey, httpProvider, IdentityServiceJsonParser)

  def authenticate(email: Option[String], password: Option[String])(implicit ec: ExecutionContext) = {
    IdentityClient.authenticateCookies(email, password).map {
      case Left(error) => Left(ServiceBadRequest(error.message))
      case Right(cookies) => Right(cookies.map { c =>
        PlayCookie(c.key, c.value, secure = true)
      })
    }
  }

}
