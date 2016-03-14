package com.gu.identity.frontend.authentication

import com.gu.identity.cookie.IdentityCookieDecoder
import com.gu.identity.frontend.logging.Logging
import controllers.routes
import play.api.mvc._
import play.api.mvc.Results.SeeOther



import scala.concurrent.Future

class UserAuthenticatedRequest[A](val scGuUCookie: Cookie, request: Request[A]) extends WrappedRequest[A](request)

object UserAuthenticatedActionBuilder extends Logging{
  def UserAuthenticatedAction(identityCookieDecoder: IdentityCookieDecoder) = new ActionRefiner[Request, UserAuthenticatedRequest] with ActionBuilder[UserAuthenticatedRequest] {
    def refine[A](request: Request[A]): Future[Either[Result, UserAuthenticatedRequest[A]]] = Future.successful {

      AuthenticationService.authenticatedUserFor(request, identityCookieDecoder.getUserDataForScGuU) match {
        case Some(authenticatedUser) => {
          getSC_GU_UCookie(request.cookies) match {
            case Some(cookie) => Right(new UserAuthenticatedRequest[A](cookie, request))
            case _ => {
              logger.error("Cookie not found on successfully authenticated request.")
              Left(SeeOther("/signin"))
            }
          }
        }
        case _ => Left(SeeOther("/signin"))
      }
    }
  }
  def getSC_GU_UCookie(cookies: Cookies): Option[Cookie] = cookies.get(CookieName.SC_GU_U.toString)
}

