package com.gu.identity.frontend.controllers

import com.gu.identity.frontend.csrf.CSRFCheck
import com.gu.identity.frontend.logging.Logging
import com.gu.identity.frontend.models.{ReturnUrl, TrackingData}
import com.gu.identity.frontend.services.{IdentityService, ServiceError, ServiceGatewayError}
import play.api.data.Form
import play.api.data.Forms.{boolean, default, mapping, optional, text}
import play.api.i18n.{MessagesApi, I18nSupport}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Action, Controller}
import play.filters.csrf.CSRFConfig

import scala.util.control.NonFatal
import play.api.i18n.Messages.Implicits._


/**
 * Form actions controller
 */
class SigninAction(identityService: IdentityService, val messagesApi: MessagesApi, csrfConfig: CSRFConfig) extends Controller with Logging with I18nSupport {

  case class SignInRequest(email: Option[String], password: Option[String], rememberMe: Boolean, returnUrl: Option[String], skipConfirmation: Option[Boolean])

  private val signInFormBody = Form(
    mapping(
      "email" -> optional(text),
      "password" -> optional(text),
      "rememberMe" -> default(boolean, false),
      "returnUrl" -> optional(text),
      "skipConfirmation" -> optional(boolean)
    )(SignInRequest.apply)(SignInRequest.unapply)
  )

  def signIn = CSRFCheck(csrfConfig).async { implicit request =>
    val formParams = signInFormBody.bindFromRequest()(request).get
    val trackingData = TrackingData(request, formParams.returnUrl)
    val returnUrl = ReturnUrl(formParams.returnUrl, request.headers.get("Referer"))

    identityService.authenticate(formParams.email, formParams.password, formParams.rememberMe, trackingData).map {
      case Left(errors) => redirectToSigninPageWithErrorsAndEmail(errors, returnUrl, formParams.skipConfirmation)
      case Right(cookies) => {
        SeeOther(returnUrl.url)
          .withCookies(cookies: _*)
      }

    }.recover {
      case NonFatal(ex) => {
        logger.warn(s"Unexpected error signing in: ${ex.getMessage}", ex)
        redirectToSigninPageWithErrorsAndEmail(Seq(ServiceGatewayError(ex.getMessage)), returnUrl, formParams.skipConfirmation)
      }
    }
  }


  private def redirectToSigninPageWithErrorsAndEmail(errors: Seq[ServiceError], returnUrl: ReturnUrl, skipConfirmation: Option[Boolean]) = {
    val query = errors.map("signin-" + _.id)
    SeeOther(routes.Application.signIn(query, Some(returnUrl.url), skipConfirmation).url)
  }

}
