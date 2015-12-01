package com.gu.identity.frontend.controllers

import javax.inject.Inject

import com.gu.identity.frontend.logging.Logging
import com.gu.identity.frontend.services.{ServiceGatewayError, ServiceError, IdentityService}
import play.api.mvc.{AnyContent, Request, Action, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.util.control.NonFatal


/**
 * Form actions controller
 */
class SigninAction @Inject() (identityService: IdentityService) extends Controller with Logging {

  private def getFormParam(name: String, request: Request[AnyContent]) =
    request.body.asFormUrlEncoded.flatMap(_.get(name)).flatMap(_.headOption)

  def signIn = Action.async { request =>
    val email = getFormParam("email", request)
    val password = getFormParam("password", request)
    val rememberMe = getFormParam("keepMeSignedIn", request).contains("true")

    identityService.authenticate(email, password, rememberMe).map {
      case Left(errors) => redirectToSigninPageWithErrors(errors)
      case Right(cookies) =>
        SeeOther(getReturnUrl(request))
          .withHeaders("Cache-Control" -> "no-cache")
          .withCookies(cookies: _*)

    }.recover {
      case NonFatal(ex) => {
        logger.warn(s"Unexpected error signing in: ${ex.getMessage}", ex)
        redirectToSigninPageWithErrors(Seq(ServiceGatewayError(ex.getMessage)))
      }
    }
  }


  private def getReturnUrl(request: Request[AnyContent]) =
    getFormParam("returnUrl", request)
      .filter(validateReturnUrl)
      .getOrElse("https://www.theguardian.com") // default



  private val urlRegex = """^https?://([^/]+).*$""".r

  def validateReturnUrl(returnUrl: String) =
    returnUrl match {
      case urlRegex(domain) if domain.endsWith(".theguardian.com") => true
      case _ => false
    }

  private def redirectToSigninPageWithErrors(errors: Seq[ServiceError]) = {
    val query = errors.map(e => s"error=${e.message}").mkString("&")
    SeeOther(routes.Application.signIn().url + s"?$query")
      .withHeaders("Cache-Control" -> "no-cache")
  }

}
