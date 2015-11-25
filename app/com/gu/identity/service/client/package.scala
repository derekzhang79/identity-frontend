package com.gu.identity.service

import org.joda.time.DateTime


package object client {

  case class IdentityClientConfiguration(host: String, apiKey: String)

  sealed trait IdentityClientError {
    val message: String
  }

  case class GatewayError(cause: Throwable, message: String) extends IdentityClientError
  case class BadRequest(message: String) extends IdentityClientError

  case class Cookie(key: String, value: String, isSession: Boolean, expires: DateTime)

  case class AuthenticateRequest(email: String, password: String)

  object AuthenticateRequest {
    private def isValidEmail(email: String): Boolean =
      email.nonEmpty

    private def isValidPassword(password: String): Boolean =
      password.nonEmpty

    def from(email: Option[String], password: Option[String]): Either[BadRequest, AuthenticateRequest] =
      (email, password) match {
        case (Some(e), Some(p)) if isValidEmail(e) && isValidPassword(p) => Right(AuthenticateRequest(e, p))
        case _ => Left(BadRequest("Invalid request"))
      }


  }

}
