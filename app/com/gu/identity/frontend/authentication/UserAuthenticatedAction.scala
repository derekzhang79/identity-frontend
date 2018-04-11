package com.gu.identity.frontend.authentication

import java.net.URI

import com.gu.identity.cookie.IdentityCookieDecoder
import com.gu.identity.frontend.logging.Logging
import com.gu.identity.frontend.models.{ClientID, GroupCode}
import com.gu.identity.frontend.controllers._
import com.gu.identity.model.User
import play.api.mvc._
import play.api.mvc.Results.SeeOther



import scala.concurrent.Future
import scala.util.{Success, Try}

class UserAuthenticatedRequest[A](val scGuUCookie: Cookie, request: Request[A]) extends WrappedRequest[A](request)

object UserAuthenticatedActionBuilder extends Logging{
  def UserAuthenticatedAction(cookieDecoder: String => Option[User]) = new ActionRefiner[Request, UserAuthenticatedRequest] with ActionBuilder[UserAuthenticatedRequest] {
    def refine[A](request: Request[A]): Future[Either[Result, UserAuthenticatedRequest[A]]] = Future.successful {

      val returnUrl = request.getQueryString("returnUrl")
      val skipConfirmation = request.getQueryString("skipConfirmation").map(_.toBoolean)
      val skipConsentJourney = request.getQueryString("skipConsentJourney").map(_.toBoolean)
      val clientId = ClientID(request.getQueryString("clientId"))
      val groupCode = getGroupCode(request.uri)

      AuthenticationService.authenticatedUserFor(request, cookieDecoder) match {
        case Some(authenticatedUser) => {
          getSC_GU_UCookie(request.cookies) match {
            case Some(cookie) => Right(new UserAuthenticatedRequest[A](cookie, request))
            case _ => {
              logger.error("Cookie not found on successfully authenticated request.")
              Left(SeeOther(routes.Application.signIn(Seq.empty, returnUrl, skipConfirmation, skipConsentJourney, clientId.map(_.id), groupCode.map(_.id)).url))
            }
          }
        }
        case _ => Left(SeeOther(routes.Application.signIn(Seq.empty, returnUrl, skipConfirmation, skipConsentJourney, clientId.map(_.id), groupCode.map(_.id)).url))
      }
    }
  }
  def getSC_GU_UCookie(cookies: Cookies): Option[Cookie] = cookies.get(CookieName.SC_GU_U.toString)

  def getGroupCode(url: String): Option[GroupCode] = {
    Try(new URI(url)) match {
      case Success(uri) => extractGroupCodeFromURI(uri)
      case _ => None
    }
  }

  def extractGroupCodeFromURI(uri: URI): Option[GroupCode] = {
    val pathComponents = uri.getPath.split("/")
    if(pathComponents.size == 3 && pathComponents.contains("agree")){
      GroupCode(pathComponents.last)
    } else {
      None
    }
  }
}

