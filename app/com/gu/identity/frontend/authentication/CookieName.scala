package com.gu.identity.frontend.authentication


object CookieName extends Enumeration {

  type Name = Value
  val GU_U, SC_GU_U, GU_ID_CSRF, gu_user_features_expiry, gu_paying_member = Value

}

case class GuardianCookie(name: CookieName.Name, secure: Boolean)
