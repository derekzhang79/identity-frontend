package com.gu.identity.frontend.controllers

import javax.inject.Inject

import com.gu.identity.frontend.logging.Logging
import com.gu.identity.frontend.services.IdentityService
import play.api.mvc.{AnyContent, Request, Action, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future
import scala.util.control.NonFatal


/**
 * Form actions controller
 */
class Actions @Inject() (identityService: IdentityService) extends Controller with Logging {

  private def getFormParam(name: String)(implicit request: Request[AnyContent]) =
    request.body.asFormUrlEncoded.flatMap(_.get(name)).flatMap(_.headOption)

  def signIn = Action.async { implicit req =>
    val email = getFormParam("email")
    val password = getFormParam("password")

    identityService.authenticate(email, password).map { r =>
      r.fold(e => BadRequest(e.toString), cookies => Ok("all good with cookies").withCookies(cookies: _*))

    }.recover {
      case NonFatal(ex) => BadGateway(s"API error: ${ex.getMessage}")
    }
  }

}
