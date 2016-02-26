package com.gu.identity.frontend.controllers

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.csrf.{CSRFConfig, CSRFCheck}
import com.gu.identity.frontend.logging.{MetricsLoggingActor, Logging}
import com.gu.identity.frontend.models.{ClientID, ReturnUrl, TrackingData}
import com.gu.identity.frontend.models.ClientID.FormMappings.{clientId => clientIdMapping}
import com.gu.identity.frontend.services._
import play.api.data.Form
import play.api.data.Forms.{boolean, default, mapping, optional, text}
import play.api.i18n.{MessagesApi, I18nSupport}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{RequestHeader, Controller}

import scala.concurrent.Future
import scala.util.control.NonFatal
import play.api.i18n.Messages.Implicits._


/**
 * Form actions controller
 */
class SigninAction(identityService: IdentityService, val messagesApi: MessagesApi, csrfConfig: CSRFConfig, googleRecaptchaCheck: GoogleRecaptchaCheck, config: Configuration) extends Controller with Logging with MetricsLoggingActor with I18nSupport {

  case class SignInRequest(
    email: Option[String],
    password: Option[String],
    rememberMe: Boolean,
    returnUrl: Option[String],
    skipConfirmation: Option[Boolean],
    googleRecaptchaResponse: Option[String],
    clientID: Option[ClientID])

  private val signInFormBody = Form(
    mapping(
      "email" -> optional(text),
      "password" -> optional(text),
      "rememberMe" -> default(boolean, false),
      "returnUrl" -> optional(text),
      "skipConfirmation" -> optional(boolean),
      "g-recaptcha-response" -> optional(text),
      "clientId" -> optional(clientIdMapping)
    )(SignInRequest.apply)(SignInRequest.unapply)
  )

  def signIn = CSRFCheck(csrfConfig, handleCSRFError).async { implicit request =>
    val formParams = signInFormBody.bindFromRequest()(request).get
    val trackingData = TrackingData(request, formParams.returnUrl)
    val returnUrl = ReturnUrl(formParams.returnUrl, request.headers.get("Referer"), config)

    def googleRecaptchaError = Future.successful(
      redirectToSigninPageWithErrorsAndEmail(Seq(ServiceBadRequest("error-captcha")), returnUrl, formParams.skipConfirmation, formParams.clientID)
    )

    googleRecaptchaCheck(formParams.googleRecaptchaResponse, googleRecaptchaError) {
      identityService.authenticate(formParams.email, formParams.password, formParams.rememberMe, trackingData).map {
        case Left(errors) => redirectToSigninPageWithErrorsAndEmail(errors, returnUrl, formParams.skipConfirmation, formParams.clientID)
        case Right(cookies) => {
          logSuccessfulSignin
          SeeOther(returnUrl.url)
            .withCookies(cookies: _*)
        }
      }.recover {
        case NonFatal(ex) => {
          logger.warn(s"Unexpected error signing in: ${ex.getMessage}", ex)
          redirectToSigninPageWithErrorsAndEmail(Seq(ServiceGatewayError(ex.getMessage)), returnUrl, formParams.skipConfirmation, formParams.clientID)
        }
      }
    }
  }

  private def redirectToSigninPageWithErrorsAndEmail(errors: Seq[ServiceError], returnUrl: ReturnUrl, skipConfirmation: Option[Boolean], clientID: Option[ClientID]) = {
    val query = errors.map("signin-" + _.id)
    SeeOther(routes.Application.signIn(query, Some(returnUrl.url), skipConfirmation, clientID.map(_.id)).url)
  }


  // Note: Limitation
  //       Error Handler only accepts RequestHeader instead of Request, so we cannot
  //       pass ReturnUrl and skipConfirmation as they're on the Request body.
  private def handleCSRFError(request: RequestHeader, msg: String) = Future.successful {
    logger.error(s"CSRF error during Sign-in: $msg")
    val errors = Seq("signin-error-csrf")

    SeeOther(routes.Application.signIn(errors).url)
  }

}

