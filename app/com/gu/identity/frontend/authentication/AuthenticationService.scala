package com.gu.identity.frontend.authentication

import com.gu.identity.frontend.authentication.CookieName.Name
import com.gu.identity.model.User
import play.api.mvc.{Result, Cookie, DiscardingCookie, RequestHeader}
import play.api.mvc.Results._



object AuthenticationService {

  implicit def cookieNameToString(cookieName: Name): String = cookieName.toString

  def authenticatedUserFor[A](request: RequestHeader, cookieDecoder: String => Option[User]): Option[AuthenticatedUser] = for {
    scGuU <- request.cookies.get(CookieName.SC_GU_U)
    minimalSecureUser <- cookieDecoder(scGuU.value)
    userId = minimalSecureUser.getId
  } yield AuthenticatedUser(userId)


  def deauthenticate(
      request: RequestHeader,
      verifiedReturnUrl: String,
      cookieDomain: String,
      newCookies: Seq[Cookie] = Seq.empty): Result = {

    val cookiesToDiscard: Seq[DiscardingCookie] = Seq(
      GuardianCookie(CookieName.gu_user_features_expiry, secure = false),
      GuardianCookie(CookieName.gu_paying_member, secure = false),
      GuardianCookie(CookieName.GU_U, secure = false),
      GuardianCookie(CookieName.SC_GU_U, secure = true),
      GuardianCookie(CookieName.GU_ID_CSRF, secure = true)
    ).map(cookie => DiscardingCookie(cookie.name, "/", Some(cookieDomain), secure = cookie.secure))

    Found(verifiedReturnUrl)
      .withHeaders("Cache-Control" -> "no-cache", "Pragma" -> "no-cache")
      .discardingCookies(cookiesToDiscard:_*)
      .withCookies(newCookies: _*)
  }
}

case class AuthenticatedUser(userId: String)
