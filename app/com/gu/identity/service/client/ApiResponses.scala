package com.gu.identity.service.client

import org.joda.time.DateTime

sealed trait ApiResponse

case class AuthenticationCookiesResponse(cookies: AuthenticationCookiesResponseCookieList) extends ApiResponse
case class AuthenticationCookiesResponseCookieList(expiresAt: DateTime, values: List[AuthenticationCookiesResponseCookie])
case class AuthenticationCookiesResponseCookie(key: String, value: String, sessionCookie: Option[Boolean] = None)

