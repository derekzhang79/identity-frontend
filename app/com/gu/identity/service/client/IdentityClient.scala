package com.gu.identity.service.client

import scala.concurrent.Future


class IdentityClient(configuration: IdentityClientConfiguration) {

  def authenticate(email: Option[String], password: Option[String]): Future[Either[IdentityClientError, Seq[Cookie]]] =
    AuthenticateRequest.from(email, password) match {
      case Right(request) => authenticate(request)
      case Left(err) => Future.successful(Left(err))
    }

  def authenticate(request: AuthenticateRequest): Future[Either[IdentityClientError, Seq[Cookie]]] = {
    Future.failed(new NotImplementedError("TODO"))
  }

}

object IdentityClient {
  def apply(host: String, apiKey: String): IdentityClient =
    new IdentityClient(IdentityClientConfiguration(host, apiKey))
}
