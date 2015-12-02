package com.gu.identity.frontend.controllers

import javax.inject.Inject

import com.gu.identity.frontend.logging.Logging
import com.gu.identity.frontend.services.{IdentityService, ServiceError, ServiceGatewayError}
import play.api.data.Form
import play.api.data.Forms.{boolean, default, mapping, optional, text}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Action, Controller}

import scala.util.control.NonFatal


/**
 * Form actions controller
 */
class SigninAction @Inject() (identityService: IdentityService) extends Controller with Logging {

  case class SignInRequest(email: Option[String], password: Option[String], rememberMe: Boolean, returnUrl: Option[String])

  private val signInFormBody = Form(
    mapping(
      "email" -> optional(text),
      "password" -> optional(text),
      "rememberMe" -> default(boolean, false),
      "returnUrl" -> optional(text)
    )(SignInRequest.apply)(SignInRequest.unapply)
  )


  def signIn = Action.async { request =>
  NoCache {
    val formParams = signInFormBody.bindFromRequest()(request).get

    identityService.authenticate(formParams.email, formParams.password, formParams.rememberMe).map {
      case Left(errors) => redirectToSigninPageWithErrors(errors)
      case Right(cookies) =>
        SeeOther(normaliseReturnUrl(formParams.returnUrl))
          .withCookies(cookies: _*)

    }.recover {
      case NonFatal(ex) => {
        logger.warn(s"Unexpected error signing in: ${ex.getMessage}", ex)
        redirectToSigninPageWithErrors(Seq(ServiceGatewayError(ex.getMessage)))
      }
    }
  }
  }


  private def normaliseReturnUrl(returnUrl: Option[String]) =
    returnUrl
      .filter(validateReturnUrl)
      .getOrElse("https://www.theguardian.com") // default



  private val urlRegex = """^https?://([^/]+).*$""".r

  private def validateReturnUrl(returnUrl: String) =
    returnUrl match {
      case urlRegex(domain) if domain.endsWith(".theguardian.com") => true
      case _ => false
    }

  private def redirectToSigninPageWithErrors(errors: Seq[ServiceError]) = {
    val query = errors.map(e => s"error=${e.message}").mkString("&")
    SeeOther(routes.Application.signIn().url + s"?$query")
  }

}
