package com.gu.identity.frontend.services

import javax.inject.Inject

import com.google.inject.ImplementedBy
import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.service.client.{Cookie, IdentityClient}
import org.joda.time.DateTime

import scala.concurrent.{ExecutionContext, Future}


/**
 * Adapter for the identity service client.
 */
@ImplementedBy(classOf[IdentityServiceImpl])
trait IdentityService {
  def authenticate(email: Option[String], password: Option[String])(implicit ec: ExecutionContext): Future[Either[ServiceError, Seq[IdentityCookie]]]
}


case class IdentityCookie(key: String, value: String, isSession: Boolean, expires: DateTime)



class IdentityServiceImpl @Inject() (config: Configuration) extends IdentityService {

  val client = IdentityClient(config.identityApiHost, config.identityApiKey)

  def authenticate(email: Option[String], password: Option[String])(implicit ec: ExecutionContext): Future[Either[ServiceError, Seq[IdentityCookie]]] = {
    client.authenticate(email, password).map { response =>
      Left(ServiceBadRequest("TODO!"))
    }
  }

}
