package com.gu.identity.frontend.authentication


object CookieName extends Enumeration {

  type Name = Value
  val GU_U, SC_GU_U, SC_GU_LA, GU_SO, GU_ID_CSRF, GU_PROFILE_CSRF, gu_user_features_expiry, gu_paying_member = Value
  val secureCookies = List(GU_U, SC_GU_U, SC_GU_LA, GU_ID_CSRF, GU_PROFILE_CSRF)

  def isSecureCookie(cookieName: String) = secureCookies.map(_.toString).contains(cookieName)

}
