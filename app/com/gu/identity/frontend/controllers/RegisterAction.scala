package com.gu.identity.frontend.controllers


import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.logging.Logging
import com.gu.identity.frontend.models.{UrlBuilder, ClientRegistrationIp, TrackingData, ReturnUrl}
import com.gu.identity.frontend.services.{ServiceGatewayError, ServiceError, IdentityService}
import play.api.data.{Mapping, Form}
import play.api.data.Forms._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller, Cookie => PlayCookie}

import scala.concurrent.Future
import scala.util.control.NonFatal

case class RegisterRequest(
    firstName: String,
    lastName: String,
    email: String,
    username: String,
    password: String,
    receiveGnmMarketing: Boolean,
    receive3rdPartyMarketing: Boolean,
    returnUrl: Option[String],
    skipConfirmation: Option[Boolean],
    group: Option[String])

class RegisterAction(identityService: IdentityService, val messagesApi: MessagesApi, val config: Configuration) extends Controller with Logging with I18nSupport {

  private val username: Mapping[String] = text.verifying(
    "error.username", name => name.matches("[A-z0-9]+") && name.length > 5 && name.length < 21
  )

  private val password: Mapping[String] = text.verifying(
    "error.password", name => name.length > 5 && name.length < 21
  )

  val registerForm = Form(
    mapping(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> email,
      "username" -> username,
      "password" -> password,
      "receiveGnmMarketing" -> boolean,
      "receive3rdPartyMarketing" -> boolean,
      "returnUrl" -> optional(text),
      "skipConfirmation" -> optional(boolean),
      "group" -> optional(text)
    )(RegisterRequest.apply)(RegisterRequest.unapply)
  )

  def register = Action.async { implicit request =>
    NoCache {
      val clientIp = ClientRegistrationIp(request)
      registerForm.bindFromRequest.fold(
        errorForm => {
          val errors = errorForm.errors.map(error => s"register-error-${error.key}")
          Future.successful(SeeOther(routes.Application.register(errors).url))},
        successForm => {
          val trackingData = TrackingData(request, successForm.returnUrl)
          val returnUrl = ReturnUrl(successForm.returnUrl, request.headers.get("Referer"))
          identityService.registerThenSignIn(successForm, clientIp, trackingData).map {
            case Left(errors) =>
              redirectToRegisterPageWithErrors(errors, returnUrl, successForm.skipConfirmation, successForm.group)
            case Right(cookies) => {
              registerSuccessRedirectUrl(cookies, returnUrl, successForm.skipConfirmation, successForm.group)
            }
          }.recover {
            case NonFatal(ex) => {
              logger.warn(s"Unexpected error while registering: ${ex.getMessage}", ex)
              redirectToRegisterPageWithErrors(Seq(ServiceGatewayError(ex.getMessage)), returnUrl, successForm.skipConfirmation, successForm.group)
            }

          }
        }
      )
    }
  }

  private def redirectToRegisterPageWithErrors(errors: Seq[ServiceError], returnUrl: ReturnUrl, skipConfirmation: Option[Boolean], group: Option[String]) = {

    val idErrors = errors.map{
      error => "error" -> (checkUserDataIsUnique(error))
    }
    val params = Seq(
      "returnUrl" -> returnUrl.url,
      "skipConfirmation" -> skipConfirmation.map(_.toString).getOrElse(""),
      "group" -> group.getOrElse("")
    ) ++ idErrors
    SeeOther(
      UrlBuilder(routes.Application.register(), params)
    )
  }

  private def checkUserDataIsUnique(error: ServiceError): String = {
    error.message match {
      case "Username in use" => "register-error-username-in-use"
      case "Email in use" => "register-error-email-in-use"
      case _ => s"register-${error.id}"
    }
  }

  private def registerSuccessRedirectUrl(cookies: Seq[PlayCookie], returnUrl: ReturnUrl, skipConfirmation: Option[Boolean], group: Option[String]) = {
    val groupCode = validateGroupCode(group)
    (groupCode, skipConfirmation.getOrElse(false)) match {
      case(Some(group), false) => {
        val skipConfirmationUrl = UrlBuilder(routes.Application.confirm(), Seq("returnUrl" -> returnUrl.url))
        val url = build3rdPartyUrl(group, skipConfirmationUrl, skipConfirmation = false)
        registerSuccessResult(url, cookies)
      }
      case(Some(group), true) => {
        val url = build3rdPartyUrl(group, returnUrl.url, skipConfirmation = true)
        registerSuccessResult(url, cookies)
      }
      case (None, false) => {
        val url = UrlBuilder(routes.Application.confirm(), Seq("returnUrl" -> returnUrl.url))
        registerSuccessResult(url, cookies)
      }
      case (None, true) => {
        registerSuccessResult(returnUrl.url, cookies)
      }
    }
  }

  private def registerSuccessResult(url: String, cookies: Seq[PlayCookie]) = {
    SeeOther(url).withCookies(cookies: _*)
  }

  private def validateGroupCode(group: Option[String]): Option[String] = {
    group match {
      case Some("GRS") => Some("GRS")
      case Some("GTNF") => Some("GTNF")
      case _ => None
    }
  }

  private def build3rdPartyUrl(group: String, returnUrl: String, skipConfirmation: Boolean) = {
    val baseUrl = s"${config.identityProfileBaseUrl}/agree/$group"
    val params = Seq(
      "returnUrl" -> returnUrl,
      "skipConfirmation" -> skipConfirmation.toString,
      "skipThirdPartyLandingPage" -> "true"
    )
    UrlBuilder(baseUrl, params)
  }
}
