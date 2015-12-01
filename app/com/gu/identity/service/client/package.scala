package com.gu.identity.service

import org.joda.time.DateTime

import scala.concurrent.Future
import scala.util.Try


package object client {

  type HttpParameters = Iterable[(String, String)]

  type IdentityClientErrors = Seq[IdentityClientError]

  trait IdentityClientRequestHandler {
    def handleRequest(request: ApiRequest): Future[Either[IdentityClientErrors, ApiResponse]]
  }

  case class IdentityClientConfiguration(host: String, apiKey: String, httpProvider: HttpProvider, jsonParser: JsonParser, requestHandler: IdentityClientRequestHandler)


  trait JsonParser {
    def extractAuthenticationCookiesResponse(body: String): Try[AuthenticationCookiesResponse]
    def extractErrorResponse(body: String): Try[ApiErrorResponse]
  }


  case class IdentityCookie(key: String, value: String, isSession: Boolean, expires: DateTime)

}
