package com.gu.identity.frontend.controllers

import com.gu.identity.frontend.logging.Logging
import com.gu.identity.frontend.models.{ClientRegistrationIp, TrackingData, ReturnUrl}
import com.gu.identity.frontend.services.IdentityService
import play.api.data.{Mapping, Form}
import play.api.data.Forms._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Cookie => PlayCookie, Result, Action, Controller}

import scala.concurrent.Future
import scala.util.control.NonFatal

case class RegisterRequest(
                            firstName: String,
                            lastName: String,
                            email: String,
                            username: String,
                            password: String,
                            receiveGnmMarketing: Boolean,
                            receive3rdPartyMarketing: Boolean,
                            returnUrl: Option[String]
                            )

class RegisterAction(identityService: IdentityService, val messagesApi: MessagesApi) extends Controller with Logging with I18nSupport {

  val registerForm = Form(
    mapping(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> email,
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
      "receiveGnmMarketing" -> boolean,
      "receive3rdPartyMarketing" -> boolean,
      "returnUrl" -> optional(text)
    )(RegisterRequest.apply)(RegisterRequest.unapply)
  )

  def register = Action.async { implicit request =>
    NoCache {
      val clientIp = ClientRegistrationIp(request)
      registerForm.bindFromRequest.fold(
        errorForm => Future.successful(SeeOther(routes.Application.register(Seq("error-registration"), None).url)),
        successForm => {
          val trackingData = TrackingData(request, successForm.returnUrl)
          val returnUrl = ReturnUrl(successForm.returnUrl, request.headers.get("Referer"))

          identityService.register(successForm, clientIp).flatMap {
            case Left(errors) =>
              Future.successful(SeeOther(routes.Application.register(Seq("error-registration"), Some(returnUrl.url)).url))
            case Right(cookies) => {
              signIn(successForm.email, successForm.password, trackingData)
                .map(signInCookies => {
                  val allCookies = cookies ++ signInCookies
                  SeeOther(returnUrl.url).withCookies(allCookies: _*)
                })
            }
          }.recover {
            case NonFatal(ex) => {
              logger.warn(s"Unexpected error while registering: ${ex.getMessage}", ex)
              SeeOther(routes.Application.register(Seq("error-registration"), None).url)
            }

          }
        }
      )
    }
  }

  private def signIn(email: String, password: String, trackingData: TrackingData): Future[Seq[PlayCookie]] = {
    identityService.authenticate(Some(email), Some(password), true, trackingData)
    .map {
      case Right(cookies) => cookies
      case Left(errors) => {
        logger.warn(s"User could not be logged in after registration ${errors}")
        Seq.empty
      }
    }
  }
}
