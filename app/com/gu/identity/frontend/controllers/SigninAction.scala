package com.gu.identity.frontend.controllers

import com.gu.identity.frontend.analytics.AnalyticsEventActor
import com.gu.identity.frontend.analytics.client.{SigninEventRequest, SigninFirstStepEventRequest}
import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.csrf.{CSRFCheck, CSRFConfig}
import com.gu.identity.frontend.errors.{RedirectOnError, ResultOnError, UnexpectedAppException}
import com.gu.identity.frontend.logging.{LogOnErrorAction, Logging, MetricsLoggingActor}
import com.gu.identity.frontend.models._
import com.gu.identity.frontend.request.SignInActionRequestBody
import com.gu.identity.frontend.services._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import Configuration.Environment._
import com.gu.identity.frontend.authentication.CookieService
import com.gu.tip.Tip
import java.net.URLEncoder.encode

import com.gu.identity.model.CurrentUser

import scala.concurrent.Future

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

  val signInSecondStepCurrentRedirectRoute: String = routes.Application.twoStepSignInStepTwo(CurrentUser.name).url

  val SignInServiceAction =
    ServiceAction andThen
    RedirectOnError(redirectRoute) andThen
    LogOnErrorAction(logger) andThen
    CSRFCheck(csrfConfig)

  val signInSecondStepCurrentServiceAction =
      ServiceAction andThen
      RedirectOnError(signInSecondStepCurrentRedirectRoute) andThen
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

  def signInFirstStepMetricsLogger(request: Request[SignInActionRequestBody]) = {
    metricsActor.logSuccessfulSigninFirstStep()

    if(request.body.gaClientId.isDefined) {
      eventActor.sendSuccessfulSigninFirstStep(SigninFirstStepEventRequest(request, config.gaUID))
    } else {
      logger.warn("No GA Client ID passed for sign in request")
    }
  }

  def signInSecondStepCurrent = signInSecondStepCurrentServiceAction(bodyParser) {
    signInAction(successfulSignInResponse, successfulAjaxSignInResponse, signInMetricsLogger)
  }

  def signIn = SignInServiceAction(bodyParser) {
    signInAction(successfulSignInResponse, successfulAjaxSignInResponse, signInMetricsLogger)
  }

  def signInWithSmartLock = SignInSmartLockServiceAction(bodyParser) {
    signInAction(successfulSmartLockSignInResponse, successfulAjaxSignInResponse, _ => metricsActor.logSuccessfulSmartLockSignin())
  }

  def emailSignInFirstStep = SignInServiceAction(bodyParser) {
    emailSignInFirstStepAction(successfulFirstStepResponse, signInFirstStepMetricsLogger)
  }

  def emailSignInFirstStepAction(successResponse: (String, ReturnUrl, Seq[Cookie], Option[Boolean], Option[ClientID], Option[GroupCode]) => Result, metricsLogger: (Request[SignInActionRequestBody]) => Unit) = { implicit request: Request[SignInActionRequestBody] =>
    val body = request.body

    lazy val returnUrl = body.returnUrl.getOrElse(ReturnUrl.defaultForClient(config, body.clientId))

    val successfulReturnUrl = body.groupCode match {
      case Some(groupCode) =>
        UrlBuilder.buildThirdPartyReturnUrl(returnUrl, body.skipConfirmation, skipThirdPartyLandingPage = true, body.clientId, groupCode, config)
      case _ => returnUrl
    }

   identityService.getUserType(body).map {
      case Left(errors) =>
        Left(errors)

      case Right(response) =>
        metricsLogger(request)
        val emailLoginCookie = CookieService.signInEmailCookies(body.email)(config)
        Right(successResponse(response.userType, successfulReturnUrl, emailLoginCookie, body.skipConfirmation, body.clientId, body.groupCode))
    }
  }

  def signInAction(successResponse: (ReturnUrl, Seq[Cookie]) => Result, successAjaxResponse: (ReturnUrl, Seq[Cookie]) => Result, metricsLogger: (Request[SignInActionRequestBody]) => Unit) = { implicit request: Request[SignInActionRequestBody] =>
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
        if(request.headers.toSimpleMap.contains("x-gu-browser-rq")){
          successAjaxResponse(successfulReturnUrl, cookies)
        } else {
          successResponse(successfulReturnUrl, cookies)
        }
      }
    }
  }

  val TokenFromServiceAction: ServiceActionBuilder[Request] =
    ServiceAction andThen
      RedirectOnError(redirectRoute) andThen
      LogOnErrorAction(logger)

  def permissionAuth(token:String, journey: Option[String]) = {
    TokenFromServiceAction {
      permissionAuthAction(successfulSignInResponse, token, journey)
    }
  }

  def permissionAuthAction(successResponse: (ReturnUrl, Seq[Cookie]) => Result, token:String, journeyOpt: Option[String]) = { implicit req: RequestHeader =>

    val journey = journeyOpt.getOrElse("repermission")
    val permissionRedirectString =  s"${config.identityProfileBaseUrl}/consent?journey=${journey}"
    val returnUrl = ReturnUrl(Some(permissionRedirectString), config)

    val trackingData = TrackingData(req, returnUrl.toStringOpt)

    identityService.authenticate(token, trackingData).map {
      case Left(errors) => Left(errors)
      case Right(cookies) => Right(successResponse(returnUrl, cookies))
    }
  }

  def successfulSignInResponse(successfulReturnUrl: ReturnUrl, cookies: Seq[Cookie]): Result =
    SeeOther(successfulReturnUrl.url)
      .withCookies(cookies: _*)


  def successfulAjaxSignInResponse(successfulReturnUrl: ReturnUrl, cookies: Seq[Cookie]): Result =
    Ok(s"""{"status": true, "returnUrl": "${successfulReturnUrl.url}"}""")
      .withCookies(cookies: _*)


  def successfulFirstStepResponse(userType: String, successfulReturnUrl: ReturnUrl, cookies: Seq[Cookie], skipConfirmation: Option[Boolean], clientId: Option[ClientID], group: Option[GroupCode]): Result ={
    val secondStepUrl = UrlBuilder(s"${config.identityProfileBaseUrl}/signin/$userType", Some(successfulReturnUrl), skipConfirmation, clientId, group)
    SeeOther(secondStepUrl)
      .withCookies(cookies: _*)
  }

  def successfulSmartLockSignInResponse(successfulReturnUrl: ReturnUrl, cookies: Seq[Cookie]): Result =
    Ok("")
      .withCookies(cookies: _*)
}

