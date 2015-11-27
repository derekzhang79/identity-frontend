package com.gu.identity.frontend.services

import com.gu.identity.service.client._
import play.api.libs.json.Json
import play.api.libs.json.Reads.jodaDateReads

import scala.util.Try


object IdentityServiceJsonParser extends JsonParser {

  implicit val dateReads = jodaDateReads("yyyy-MM-dd'T'HH:mm:ssZ")

  override def extractAuthenticationCookiesResponse(body: String): Try[AuthenticationCookiesResponse] = Try {
    implicit val responseCookieReads = Json.format[AuthenticationCookiesResponseCookie]
    implicit val responseCookiesListReads = Json.format[AuthenticationCookiesResponseCookieList]
    implicit val responseReads = Json.format[AuthenticationCookiesResponse]

    Json.parse(body).as[AuthenticationCookiesResponse]
  }

  override def extractErrorResponse(body: String): Try[ApiErrorResponse] = Try {
    implicit val apiErrorResponseErrorReads = Json.format[ApiErrorResponseError]
    implicit val apiErrorResponseReads = Json.format[ApiErrorResponse]

    Json.parse(body).as[ApiErrorResponse]
  }
}
