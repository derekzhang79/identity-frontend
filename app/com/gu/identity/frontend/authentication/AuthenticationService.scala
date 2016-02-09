package com.gu.identity.frontend.authentication

import com.gu.identity.cookie.IdentityCookieDecoder
import play.api.mvc.RequestHeader

class AuthenticationService(cookieDecoder: IdentityCookieDecoder) {

  def authenticatedUserFor[A](request: RequestHeader): Option[AuthenticatedUser] = for {
    scGuU <- request.cookies.get("SC_GU_U")
    minimalSecureUser <- cookieDecoder.getUserDataForScGuU(scGuU.value)
    userId = minimalSecureUser.getId
  } yield AuthenticatedUser(userId)

}

case class AuthenticatedUser(userId: String)
