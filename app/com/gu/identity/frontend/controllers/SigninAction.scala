package com.gu.identity.frontend.controllers

import com.gu.identity.frontend.logging.Logging
import com.gu.identity.frontend.models.{ReturnUrl, TrackingData}
import com.gu.identity.frontend.services.{IdentityService, ServiceError, ServiceGatewayError}
import play.api.data.Form
import play.api.data.Forms.{boolean, default, mapping, optional, text}
import play.api.i18n.{MessagesApi, I18nSupport}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Request, AnyContent, Action, Controller}

import scala.util.control.NonFatal
import play.api.i18n.Messages.Implicits._


/**
 * Form actions controller
 */
class SigninAction(identityService: IdentityService, val messagesApi: MessagesApi) extends Controller with Logging with I18nSupport {

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

  def signIn = Action.async { implicit request =>
    NoCache {
      val formParams = signInFormBody.bindFromRequest()(request).get
      val trackingData = TrackingData(request, formParams.returnUrl)
      val returnUrl = ReturnUrl(request, formParams.returnUrl)

      identityService.authenticate(formParams.email, formParams.password, formParams.rememberMe, trackingData).map {
        case Left(errors) => redirectToSigninPageWithErrorsAndEmail(errors, formParams.email, returnUrl, formParams.skipConfirmation)
        case Right(cookies) => {
          SeeOther(returnUrl.url)
            .withCookies(cookies: _*)
        }

      }.recover {
        case NonFatal(ex) => {
          logger.warn(s"Unexpected error signing in: ${ex.getMessage}", ex)
          redirectToSigninPageWithErrorsAndEmail(Seq(ServiceGatewayError(ex.getMessage)), formParams.email, returnUrl, formParams.skipConfirmation)
        }
      }
    }
  }


  private def redirectToSigninPageWithErrorsAndEmail(errors: Seq[ServiceError], email: Option[String], returnUrl: ReturnUrl, skipConfirmation: Option[Boolean]) = {
    val query = errors.map(_.id)
    SeeOther(routes.Application.signIn(email, query, Some(returnUrl.url), skipConfirmation).url)
  }

}
