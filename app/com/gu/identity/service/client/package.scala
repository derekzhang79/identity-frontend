package com.gu.identity.service

import org.joda.time.DateTime


package object client {

  type HttpParameters = Iterable[(String, String)]


  case class IdentityClientConfiguration(host: String, apiKey: String, httpProvider: HttpProvider, jsonParser: JsonParser)


  trait JsonParser {
    def parseAuthenticationCookiesResponse(body: String): AuthenticationCookiesResponse
  }


  sealed trait IdentityClientError extends Throwable {
    val message: String
  }

  case class GatewayError(cause: Throwable, message: String) extends RuntimeException with IdentityClientError
  case class BadRequest(message: String) extends RuntimeException with IdentityClientError


  case class Cookie(key: String, value: String, isSession: Boolean, expires: DateTime)

}
