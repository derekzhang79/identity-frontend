package com.gu.identity.frontend.controllers


import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.csrf.{CSRFConfig, CSRFCheck}
import com.gu.identity.frontend.errors.RedirectOnError
import com.gu.identity.frontend.logging.{LogOnErrorAction, MetricsLoggingActor, Logging}
import com.gu.identity.frontend.models._
import com.gu.identity.frontend.request.RegisterActionRequestBody
import com.gu.identity.frontend.services.{ServiceAction, IdentityService}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Cookie => PlayCookie, Controller}


class RegisterAction(identityService: IdentityService, val messagesApi: MessagesApi, val config: Configuration, csrfConfig: CSRFConfig) extends Controller with Logging with MetricsLoggingActor with I18nSupport {

  val redirectRoute: String = routes.Application.register().url

  val RegisterServiceAction =
    ServiceAction andThen
    RedirectOnError(redirectRoute) andThen
    LogOnErrorAction(logger) andThen
    CSRFCheck(csrfConfig)

  val bodyParser = RegisterActionRequestBody.bodyParser(config)

  def register = RegisterServiceAction(bodyParser) { request =>
    val clientIp = ClientIp(request)
    val body = request.body

    val trackingData = TrackingData(request, body.returnUrl.flatMap(_.toStringOpt))
    identityService.registerThenSignIn(body, clientIp, trackingData).map {
      case Left(errors) =>
        Left(errors)
      case Right(cookies) => Right {
        registerSuccessRedirectUrl(cookies, body.returnUrl, body.skipConfirmation, body.groupCode, body.clientId)
      }
    }
  }


  private def registerSuccessRedirectUrl(cookies: Seq[PlayCookie], returnUrlOpt: Option[ReturnUrl], skipConfirmation: Option[Boolean], group: Option[GroupCode], clientId: Option[ClientID]) = {
    val returnUrl = returnUrlOpt.getOrElse(ReturnUrl.defaultForClient(config, clientId))
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
