package com.gu.identity.frontend.controllers

import com.gu.identity.cookie.IdentityCookieDecoder
import com.gu.identity.frontend.authentication.AuthenticationService
import com.gu.identity.frontend.logging.Logging
import com.gu.identity.frontend.models.GroupCodes
import com.gu.identity.frontend.services.IdentityService
import com.gu.identity.service.client.models.User
import play.api.mvc.Security.AuthenticatedBuilder
import play.api.mvc._

import scala.concurrent.{Future, ExecutionContext}

class ThirdPartyTsAndCs(identityService: IdentityService, identityCookieDecoder: IdentityCookieDecoder) extends Controller with Logging {

  implicit lazy val executionContext: ExecutionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext

  val authenticationAction = new AuthenticatedBuilder(
    AuthenticationService.authenticatedUserFor(_, identityCookieDecoder.getUserDataForScGuU),
    _ => SeeOther("/signin")
  )

  def confirm = authenticationAction.async{ implicit request => {
      Future.successful(Ok("Some Ts and Cs"))
    }
  }

  def isUserInGroup(user: User, group: String) = {
    val usersGroups = user.userGroups
    usersGroups.map(_.packageCode == group).contains(true)
  }

  def isValidGroupCode(group: String): Boolean = {
    GroupCodes.isOrderType(group)
  }
}
