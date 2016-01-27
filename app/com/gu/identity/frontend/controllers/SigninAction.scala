package com.gu.identity.frontend.controllers

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.logging.Logging
import com.gu.identity.frontend.models.{ReturnUrl, TrackingData}
import com.gu.identity.frontend.services.{ServiceBadRequest, IdentityService, ServiceError, ServiceGatewayError}
import play.api.data.Form
import play.api.data.Forms.{boolean, default, mapping, optional, text}
import play.api.i18n.{MessagesApi, I18nSupport}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.{Result, Action, Controller}

import scala.concurrent.Future
import scala.util.control.NonFatal
import play.api.i18n.Messages.Implicits._


/**
 * Form actions controller
 */
class SigninAction(identityService: IdentityService, val messagesApi: MessagesApi, ws: WSClient, configuration: Configuration) extends Controller with Logging with I18nSupport {

  implicit val googleResponseFormat = Json.format[GoogleResponse]

  case class SignInRequest(email: Option[String], password: Option[String], rememberMe: Boolean, returnUrl: Option[String], skipConfirmation: Option[Boolean], googleRecaptchaResponse: Option[String])

  private val signInFormBody = Form(
    mapping(
      "email" -> optional(text),
      "password" -> optional(text),
      "rememberMe" -> default(boolean, false),
      "returnUrl" -> optional(text),
      "skipConfirmation" -> optional(boolean),
      "g-recaptcha-response" -> optional(text)
    )(SignInRequest.apply)(SignInRequest.unapply)
  )

  def signIn = Action.async { implicit request =>
    NoCache {
      val formParams = signInFormBody.bindFromRequest()(request).get
      val trackingData = TrackingData(request, formParams.returnUrl)
      val returnUrl = ReturnUrl(formParams.returnUrl, request.headers.get("Referer"))

      formParams.googleRecaptchaResponse match {
        case Some(recaptchaResponseCode) => {
          isValidRecaptchaResponse(recaptchaResponseCode).flatMap{
            case true => authenticate(formParams.email, formParams.password, formParams.rememberMe, formParams.skipConfirmation, returnUrl, trackingData)
            case false => Future.successful(redirectToSigninPageWithErrorsAndEmail(Seq(ServiceBadRequest("error-captcha")), returnUrl, formParams.skipConfirmation))
          }
        }
        case None => {
          authenticate(formParams.email, formParams.password, formParams.rememberMe, formParams.skipConfirmation, returnUrl, trackingData)
        }
      }
    }
  }

  private def isValidRecaptchaResponse(captchaResponseCode: String): Future[Boolean] = {
    val googleResponse = checkRecaptchaCodeWithGoogle(captchaResponseCode)
    googleResponse.map {
      case GoogleResponse(true, _) => true
      case GoogleResponse(false, errors) => {
        logger.warn(s"Google Recaptcha failed to authenticate ${errors}")
        false
      }
    }.recover{
      case NonFatal(ex) => {
        logger.warn(s"Unexpected error from google recaptcha: ${ex.getMessage}", ex)
        false
      }
    }

  }

  private def checkRecaptchaCodeWithGoogle(captchaResponseCode: String): Future[GoogleResponse] = {
    ws.url("https://www.google.com/recaptcha/api/siteverify").post(
      Map("secret" -> Seq("dssddfdf"), "response" -> Seq(captchaResponseCode))
    ).map{
      googleResponse => {
        val body = googleResponse.body
        Json.parse(body).as[GoogleResponse]
      }
    }
  }

  private def authenticate(
      email: Option[String],
      password: Option[String],
      rememberMe: Boolean,
      skipConfirmation: Option[Boolean],
      returnUrl: ReturnUrl,
      trackingData: TrackingData): Future[Result] = {

    identityService.authenticate(email, password, rememberMe, trackingData).map {
      case Left(errors) => redirectToSigninPageWithErrorsAndEmail(errors, returnUrl, skipConfirmation)
      case Right(cookies) => {
        SeeOther(returnUrl.url)
          .withCookies(cookies: _*)
      }
    }.recover {
      case NonFatal(ex) => {
        logger.warn(s"Unexpected error signing in: ${ex.getMessage}", ex)
        redirectToSigninPageWithErrorsAndEmail(Seq(ServiceGatewayError(ex.getMessage)), returnUrl, skipConfirmation)
      }
    }
  }

  private def redirectToSigninPageWithErrorsAndEmail(errors: Seq[ServiceError], returnUrl: ReturnUrl, skipConfirmation: Option[Boolean]) = {
    val query = errors.map("signin-" + _.id)
    SeeOther(routes.Application.signIn(query, Some(returnUrl.url), skipConfirmation).url)
  }

}

case class GoogleResponse(success: Boolean, errorCodes: Option[List[String]])

