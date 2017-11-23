package com.gu.identity.frontend.controllers

import com.gu.identity.frontend.analytics.AnalyticsEventActor
import com.gu.identity.frontend.analytics.client.SigninEventRequest
import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.csrf.{CSRFCheck, CSRFConfig}
import com.gu.identity.frontend.errors.{RedirectOnError, ResultOnError}
import com.gu.identity.frontend.logging.{LogOnErrorAction, Logging, MetricsLoggingActor}
import com.gu.identity.frontend.models._
import com.gu.identity.frontend.request.SignInActionRequestBody
import com.gu.identity.frontend.services._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import Configuration.Environment._
import com.gu.tip.Tip

/**
 * Form actions controller
 */
class SigninAction(
    identityService: IdentityService,
    val messagesApi: MessagesApi,
    metricsActor: MetricsLoggingActor,
    eventActor: AnalyticsEventActor,
    csrfConfig: CSRFConfig,
    val config: Configuration)
  extends Controller
    with Logging
    with I18nSupport {

  val redirectRoute: String = routes.Application.signIn().url

  val SignInServiceAction =
    ServiceAction andThen
    RedirectOnError(redirectRoute) andThen
    LogOnErrorAction(logger) andThen
    CSRFCheck(csrfConfig)

  val SignInSmartLockServiceAction =
    ServiceAction andThen
      ResultOnError(redirectRoute) andThen
      LogOnErrorAction(logger) andThen
      CSRFCheck(csrfConfig)

  val bodyParser = SignInActionRequestBody.bodyParser

  def signInMetricsLogger(request: Request[SignInActionRequestBody]) = {
    metricsActor.logSuccessfulSignin()

    if(request.body.gaClientId.isDefined) {
      eventActor.sendSuccessfulSignin(SigninEventRequest(request, config.gaUID))
    } else {
      logger.warn("No GA Client ID passed for sign in request")
    }
  }

  def signIn = SignInServiceAction(bodyParser) {
    signInAction(successfulSignInResponse, signInMetricsLogger)
  }

  def signInWithSmartLock = SignInSmartLockServiceAction(bodyParser) {
    signInAction(successfulSmartLockSignInResponse, _ => metricsActor.logSuccessfulSmartLockSignin())
  }

  def signInAction(successResponse: (ReturnUrl, Seq[Cookie]) => Result, metricsLogger: (Request[SignInActionRequestBody]) => Unit) = { implicit request: Request[SignInActionRequestBody] =>
    val body = request.body

    val trackingData = TrackingData(request, body.returnUrl.flatMap(_.toStringOpt))
    lazy val returnUrl = body.returnUrl.getOrElse(ReturnUrl.defaultForClient(config, body.clientId))

    val successfulReturnUrl = body.groupCode match {
      case Some(validGroupCode) =>
        UrlBuilder.buildThirdPartyReturnUrl(returnUrl, body.skipConfirmation, skipThirdPartyLandingPage = true, body.clientId, validGroupCode, config)
      case _ => returnUrl
    }

    identityService.authenticate(body, trackingData).map {
      case Left(errors) => Left(errors)
      case Right(cookies) => Right {
        if (stage == "PROD") Tip.verify("Account Signin")
        metricsLogger(request)
        successResponse(successfulReturnUrl, cookies)
      }
    }
  }

  val TokenFromLinkServiceAction: ServiceActionBuilder[Request] =
    ServiceAction andThen
      RedirectOnError(redirectRoute) andThen
      LogOnErrorAction(logger)

  def authenticate(token:String) ={
    TokenFromLinkServiceAction {
      authenticateAction(successfulSignInResponse, token)
    }
  }


  def authenticateAction(successResponse: (ReturnUrl, Seq[Cookie]) => Result, token:String) = { implicit req: RequestHeader =>

    val trackingData = TrackingData(req, ReturnUrl.default(config).toStringOpt)

    identityService.authenticate(token, trackingData).map {
      case Left(errors) => Left(errors)
      case Right(cookies) => Right(successResponse(ReturnUrl.default(config), cookies))
    }
  }

  def successfulSignInResponse(successfulReturnUrl: ReturnUrl, cookies: Seq[Cookie]): Result =
    SeeOther(successfulReturnUrl.url)
      .withCookies(cookies: _*)

  def successfulSmartLockSignInResponse(successfulReturnUrl: ReturnUrl, cookies: Seq[Cookie]): Result =
    Ok("")
      .withCookies(cookies: _*)
}

