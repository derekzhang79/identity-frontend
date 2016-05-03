package com.gu.identity.frontend.controllers

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.csrf.{CSRFCheck, CSRFConfig}
import com.gu.identity.frontend.errors.{ResultOnError, RedirectOnError}
import com.gu.identity.frontend.logging.{LogOnErrorAction, Logging, MetricsLoggingActor}
import com.gu.identity.frontend.models._
import com.gu.identity.frontend.request.SignInActionRequestBody
import com.gu.identity.frontend.services._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.{JsString, JsBoolean, JsObject}
import play.api.mvc._


/**
 * Form actions controller
 */
class SigninAction(identityService: IdentityService, val messagesApi: MessagesApi, csrfConfig: CSRFConfig, config: Configuration) extends Controller with Logging with MetricsLoggingActor with I18nSupport {

  val redirectRoute: String = routes.Application.signIn().url

  val SignInServiceAction =
    ServiceAction andThen
    RedirectOnError(redirectRoute) andThen
    LogOnErrorAction(logger) andThen
    CSRFCheck(csrfConfig)

  val bodyParser = SignInActionRequestBody.bodyParser

  def signIn = SignInServiceAction(bodyParser) { request: Request[SignInActionRequestBody] =>
    val body = request.body

    val trackingData = TrackingData(request, body.returnUrl.flatMap(_.toStringOpt))
    lazy val returnUrl = body.returnUrl.getOrElse(ReturnUrl.defaultForClient(config, body.clientId))

    val successfulReturnUrl = body.groupCode match {
      case Some(validGroupCode) => {
        UrlBuilder.buildThirdPartyReturnUrl(returnUrl, body.skipConfirmation, skipThirdPartyLandingPage = true, body.clientId, validGroupCode, config)
      }
      case _ => returnUrl
    }


    identityService.authenticate(body, trackingData).map {
      case Left(errors) => Left(errors)
      case Right(cookies) => Right {
        logSuccessfulSignin()
        successfulSignInResponse(successfulReturnUrl, cookies)
      }
    }
  }

  val SignInSmartLockServiceAction =
    ServiceAction andThen
      ResultOnError(redirectRoute) andThen
      LogOnErrorAction(logger) andThen
      CSRFCheck(csrfConfig)

  def signInWithSmartLock = SignInSmartLockServiceAction(bodyParser) { request: Request[SignInActionRequestBody] =>
    val body = request.body

    val trackingData = TrackingData(request, body.returnUrl.flatMap(_.toStringOpt))
    lazy val returnUrl = body.returnUrl.getOrElse(ReturnUrl.defaultForClient(config, body.clientId))

    val successfulReturnUrl = body.groupCode match {
      case Some(validGroupCode) => {
        UrlBuilder.buildThirdPartyReturnUrl(returnUrl, body.skipConfirmation, skipThirdPartyLandingPage = true, body.clientId, validGroupCode, config)
      }
      case _ => returnUrl
    }


    identityService.authenticate(body, trackingData).map {
      case Left(errors) => Left(errors)
      case Right(cookies) => Right {
        logSuccessfulSignin()
        successfulSmartLockSignInResponse(successfulReturnUrl, cookies)
      }
    }
  }

  def successfulSignInResponse(successfulReturnUrl: ReturnUrl, cookies: Seq[Cookie]): Result = {
    val body: Array[Byte] = JsObject(Seq("success" -> JsBoolean(true))).toString.getBytes("UTF-8")
    Result(header = ResponseHeader(303), body = Enumerator(body))
      .withHeaders(LOCATION -> successfulReturnUrl.url)
      .withCookies(cookies: _*)
  }

  def successfulSmartLockSignInResponse(successfulReturnUrl: ReturnUrl, cookies: Seq[Cookie]): Result = {
    val body: Array[Byte] = JsObject(Seq("success" -> JsString(successfulReturnUrl.url))).toString.getBytes("UTF-8")
    Ok(body)
      .withCookies(cookies: _*)
  }
}

