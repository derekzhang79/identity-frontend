package com.gu.identity.frontend.services

import com.gu.identity.service.client._
import play.api.libs.json.Json
import play.api.libs.json.Reads.jodaDateReads


object IdentityServiceJsonParser extends JsonParser {

  implicit val dateReads = jodaDateReads("yyyy-MM-dd'T'HH:mm:ssZ")

  override def parseAuthenticationCookiesResponse(body: String): AuthenticationCookiesResponse = {
    println(body)

    implicit val responseCookieReads = Json.format[AuthenticationCookiesResponseCookie]
    implicit val responseCookiesListReads = Json.format[AuthenticationCookiesResponseCookieList]
    implicit val responseReads = Json.format[AuthenticationCookiesResponse]

    Json.parse(body).as[AuthenticationCookiesResponse]
  }
}
