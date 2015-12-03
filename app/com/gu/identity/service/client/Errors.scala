package com.gu.identity.service.client

sealed trait IdentityClientError extends Throwable {
  val message: String
  val description: Option[String]
}

case class GatewayError(message: String,
                        description: Option[String] = None,
                        context: Option[String] = None,
                        cause: Option[Throwable] = None) extends RuntimeException with IdentityClientError

case class BadRequest(message: String,
                      description: Option[String] = None,
                      context: Option[String] = None,
                      cause: Option[Throwable] = None) extends RuntimeException with IdentityClientError
