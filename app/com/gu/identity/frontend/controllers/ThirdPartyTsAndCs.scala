package com.gu.identity.frontend.controllers

import com.gu.identity.cookie.IdentityCookieDecoder
import com.gu.identity.frontend.authentication.{CookieName, AuthenticationService}
import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.models.{GroupCode, ClientID, ReturnUrl}
import com.gu.identity.frontend.services.{ServiceError, IdentityService}
import com.gu.identity.service.client.models.User
import play.api.data.Form
import play.api.data.Forms._
import play.api.http.HttpErrorHandler
import play.api.i18n.{MessagesApi, I18nSupport}
import play.api.mvc.Security.AuthenticatedBuilder
import play.api.mvc._
import com.gu.identity.frontend.logging.Logging
import com.gu.identity.frontend.views.ViewRenderer.renderTsAndCs

import scala.concurrent.{Future, ExecutionContext}

case class AddUserToGroupRequest(groupCode: String, returnUrl: Option[String])

class ThirdPartyTsAndCs(identityService: IdentityService, identityCookieDecoder: IdentityCookieDecoder, config: Configuration, val messagesApi: MessagesApi, httpErrorHandler: HttpErrorHandler) extends Controller with Logging with I18nSupport {

  implicit lazy val executionContext: ExecutionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext

  val authenticationAction = new AuthenticatedBuilder(
    AuthenticationService.authenticatedUserFor(_, identityCookieDecoder.getUserDataForScGuU),
    _ => SeeOther("/signin")
  )

  def confirm(group: String, returnUrl: Option[String], clientId: Option[String], skipConfirmation: Option[Boolean]) = authenticationAction.async{ implicit request => {
      val clientIdActual = ClientID(clientId)
      val groupCode = GroupCode(group)
      val sc_gu_uCookie = getSC_GU_UCookie(request.cookies)
      val verifiedReturnUrl = ReturnUrl(returnUrl, request.headers.get("Referer"), config)

      (groupCode, sc_gu_uCookie) match {
        case(Some(validGroup), Some(cookie)) => {
          checkUserForGroupMembership(group, cookie).map{
            case Right(true) => SeeOther(verifiedReturnUrl.url)
            case Right(false) => {
              skipConfirmation match {
                case Some(true) => SeeOther(verifiedReturnUrl.url)
                case _ => renderTsAndCs(config, clientIdActual, validGroup, verifiedReturnUrl)
              }
            }
            case Left(errors) => {
              logger.warn(s"Could not check user's group membership status {}", errors)
              BadRequest
            }
          }
        }
        case(None, _) => {
          logger.info(s"Received invalid group code $group")
          httpErrorHandler.onClientError(request, NOT_FOUND, "Invalid Group Code")
        }
        case(_, None) => {
          logger.info("Request did not have a SC_GU_U cookie")
          httpErrorHandler.onClientError(request, NOT_FOUND, "Missing Cookie")
        }
      }
    }
  }

  def checkUserForGroupMembership(group: String, cookie: Cookie): Future[Either[Seq[ServiceError], Boolean]] = {
    identityService.getUser(cookie).map{
      case Right(user) => {
        Right(isUserInGroup(user, group))
      }
      case Left(errors) => {
        logger.info("Request did not have a SC_GU_U cookie could not get user.")
        Left(errors)
      }
    }
  }


  def addToGroup(): Action[AnyContent] = authenticationAction.async { implicit request =>
    val sc_gu_uCookie = getSC_GU_UCookie(request.cookies)
    addUserToGroupRequestFormBody.bindFromRequest.fold(
      errorForm => httpErrorHandler.onClientError(request, NOT_FOUND, "Invalid form submission"),
      successForm => {
        val verifiedReturnUrl = ReturnUrl(successForm.returnUrl, config)
        GroupCode(successForm.groupCode) match {
          case Some(code) => addToGroup(code, sc_gu_uCookie, verifiedReturnUrl)
          case _ => httpErrorHandler.onClientError(request, NOT_FOUND, "Invalid Group Code")
        }
      }
    )
  }

  def addToGroup(group: GroupCode, sc_gu_uCookie: Option[Cookie], returnUrl: ReturnUrl): Future[Result] = {
    sc_gu_uCookie match {
      case Some(cookie) => {
        val response = identityService.assignGroupCode(group.getCodeValue, cookie)
        response.map{
          case Left(errors) => Ok("assign to group fails")
          case Right(response) => SeeOther(returnUrl.url)
        }
      }
      case _ => Future.successful(Ok("No cookie"))
    }
  }

  def isUserInGroup(user: User, group: String): Boolean = {
    val usersGroups = user.userGroups
    usersGroups.map(_.packageCode == group).contains(true)
  }

  def getSC_GU_UCookie(cookies: Cookies): Option[Cookie] = cookies.get(CookieName.SC_GU_U.toString)

  private val addUserToGroupRequestFormBody = Form(
    mapping(
      "groupCode" -> text,
      "returnUrl" -> optional(text)
    )(AddUserToGroupRequest.apply)(AddUserToGroupRequest.unapply)
  )

}
