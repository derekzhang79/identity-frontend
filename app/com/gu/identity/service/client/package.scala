package com.gu.identity.service

import org.joda.time.DateTime

import scala.util.Try


package object client {

  type HttpParameters = Iterable[(String, String)]

  type IdentityClientErrors = Seq[IdentityClientError]


  case class IdentityClientConfiguration(host: String, apiKey: String, httpProvider: HttpProvider, jsonParser: JsonParser)


  trait JsonParser {
    def extractAuthenticationCookiesResponse(body: String): Try[AuthenticationCookiesResponse]
    def extractErrorResponse(body: String): Try[ApiErrorResponse]
  }


  case class Cookie(key: String, value: String, isSession: Boolean, expires: DateTime)

}
