package com.gu.identity.frontend.controllers


import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.csrf.{CSRFConfig, CSRFCheck}
import com.gu.identity.frontend.errors.AppException
import com.gu.identity.frontend.logging.{MetricsLoggingActor, Logging}
import com.gu.identity.frontend.models._
import com.gu.identity.frontend.request.RegisterActionRequestBody
import com.gu.identity.frontend.services.IdentityService
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Cookie => PlayCookie, Controller}


class RegisterAction(identityService: IdentityService, val messagesApi: MessagesApi, val config: Configuration, csrfConfig: CSRFConfig) extends Controller with Logging with MetricsLoggingActor with I18nSupport {

  val bodyParser = RegisterActionRequestBody.bodyParser(config)

  def register = CSRFCheck(csrfConfig).async(bodyParser) { request =>
    val clientIp = ClientIp(request)
    val body = request.body

    val trackingData = TrackingData(request, body.returnUrl)
    val returnUrl = ReturnUrl(body.returnUrl, request.headers.get("Referer"), config, body.clientID)
    identityService.registerThenSignIn(body, clientIp, trackingData).map {
      case Left(errors) =>
        redirectToRegisterPageWithErrors(errors, returnUrl, body.skipConfirmation, body.groupCode, body.clientID)
      case Right(cookies) => {
        registerSuccessRedirectUrl(cookies, returnUrl, body.skipConfirmation, body.groupCode, body.clientID)
      }
    }
  }

  private def redirectToRegisterPageWithErrors(errors: Seq[AppException], returnUrl: ReturnUrl, skipConfirmation: Option[Boolean], group: Option[GroupCode], clientId: Option[ClientID]) = {

    val idErrors = errors.map {
      error => "error" -> (checkUserDataIsUnique(error))
    }

    val params = Seq(
      Some("returnUrl" -> returnUrl.url),
      skipConfirmation.map("skipConfirmation" -> _.toString),
      group.map("group" -> _.id),
      clientId.map("clientId" -> _.id)
    ).flatten

    SeeOther(
      UrlBuilder(routes.Application.register(), params ++ idErrors)
    )
  }

  private def checkUserDataIsUnique(error: AppException): String =
    error.id.key

  private def registerSuccessRedirectUrl(cookies: Seq[PlayCookie], returnUrl: ReturnUrl, skipConfirmation: Option[Boolean], group: Option[GroupCode], clientId: Option[ClientID]) = {
    val registrationConfirmUrl = UrlBuilder(config.identityProfileBaseUrl, routes.Application.confirm())
    (group, skipConfirmation.getOrElse(false)) match {
      case(Some(group), false) => {
        val skipConfirmationReturnUrl = ReturnUrl(Some(UrlBuilder(registrationConfirmUrl, returnUrl, clientId)), config)
        val url = UrlBuilder.buildThirdPartyReturnUrl(skipConfirmationReturnUrl, skipConfirmation, skipThirdPartyLandingPage = true, clientId, group, config)
        registerSuccessResult(url, cookies)
      }
      case(Some(group), true) => {
        val url = UrlBuilder.buildThirdPartyReturnUrl(returnUrl, skipConfirmation, skipThirdPartyLandingPage = true, clientId, group, config)
        registerSuccessResult(url, cookies)
      }
      case (None, false) => {
        val url = ReturnUrl(Some(UrlBuilder(registrationConfirmUrl, returnUrl, clientId)), config)
        registerSuccessResult(url, cookies)
      }
      case (None, true) => {
        registerSuccessResult(returnUrl, cookies)
      }
    }
  }

  private def registerSuccessResult(returnUrl: ReturnUrl, cookies: Seq[PlayCookie]) = {
    logSuccessfulRegister
    SeeOther(returnUrl.url).withCookies(cookies: _*)
  }
}
