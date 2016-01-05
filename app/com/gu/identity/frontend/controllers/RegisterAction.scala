package com.gu.identity.frontend.controllers

import com.gu.identity.frontend.logging.Logging
import com.gu.identity.frontend.services.IdentityService
import play.api.data.{Mapping, Form}
import play.api.data.Forms._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future

case class RegisterRequest(
                            firstName: String,
                            lastName: String,
                            email: String,
                            username: String,
                            password: String,
                            receiveGnmMarketing: Boolean,
                            receive3rdPartyMarketing: Boolean
                            )

class RegisterAction(identityService: IdentityService, val messagesApi: MessagesApi) extends Controller with Logging with I18nSupport {

  private val username: Mapping[String] = text.verifying(
    "error.username", name => name.matches("[A-z0-9]+") && name.length > 5 && name.length < 21
  )

  private val password: Mapping[String] = text.verifying(
    "error.password", pwd => pwd.length > 5 && pwd.length < 21
  )

  val registerForm = Form(
    mapping(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> email,
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
      "receiveGnmMarketing" -> boolean,
      "receive3rdPartyMarketing" -> boolean
    )(RegisterRequest.apply)(RegisterRequest.unapply)
  )

  def register = Action.async { implicit request =>
    NoCache {
      val clientIp = request.remoteAddress
      registerForm.bindFromRequest.fold(
        errorForm => Future(SeeOther(routes.Application.register(Seq("error-registration")).url)),
        success => identityService.register(success, clientIp).map {
          case Left(errors) => SeeOther(routes.Application.register(Seq("error-registration")).url)
          case Right(cookies) =>
            SeeOther("http://www.theguardian.com")
              .withCookies(cookies: _*)
        }
      )
    }
  }
}
