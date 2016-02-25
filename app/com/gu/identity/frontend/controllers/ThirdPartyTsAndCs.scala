package com.gu.identity.frontend.controllers

import com.gu.identity.cookie.IdentityCookieDecoder
import com.gu.identity.frontend.authentication.{CookieName, AuthenticationService}
import com.gu.identity.frontend.models.GroupCodes
import com.gu.identity.frontend.services.IdentityService
import com.gu.identity.service.client.models.User
import play.api.mvc.Security.AuthenticatedBuilder
import play.api.mvc._
import com.gu.identity.frontend.logging.Logging

import scala.concurrent.{Future, ExecutionContext}

class ThirdPartyTsAndCs(identityService: IdentityService, identityCookieDecoder: IdentityCookieDecoder) extends Controller with Logging {

  implicit lazy val executionContext: ExecutionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext

  val authenticationAction = new AuthenticatedBuilder(
    AuthenticationService.authenticatedUserFor(_, identityCookieDecoder.getUserDataForScGuU),
    _ => SeeOther("/signin")
  )

  def confirm(group: String) = authenticationAction.async{ implicit request => {
      val isValidGroup = isValidGroupCode(group)
      val sc_gu_uCookie = getSC_GU_UCookie(request.cookies)

      (isValidGroup, sc_gu_uCookie) match {
        case(true, Some(cookie)) => renderAgree()
        case(false, _) => {
          logger.info(s"Received invalid group code $group")
          Future(BadRequest)
        }
        case(_, None) => {
          logger.info("Request did not have a SC_GU_U cookie")
          Future(BadRequest)
        }
      }
    }
  }

  def renderAgree() = {
    Future.successful(Ok("Some Ts and Cs"))
  }

  def getUser(cookie: Cookie) = {
    identityService.getUser(cookie)
  }

  def getSC_GU_UCookie(cookies: Cookies): Option[Cookie] = cookies.get(CookieName.SC_GU_U.toString)

  def isUserInGroup(user: User, group: String):Boolean = {
    val usersGroups = user.userGroups
    usersGroups.map(_.packageCode == group).contains(true)
  }

  def isValidGroupCode(group: String): Boolean = {
    GroupCodes.isOrderType(group)
  }
}
