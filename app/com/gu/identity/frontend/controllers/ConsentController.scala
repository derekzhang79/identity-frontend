package com.gu.identity.frontend.controllers

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.errors.NotFoundError
import com.gu.identity.frontend.logging.Logging
import com.gu.identity.frontend.services.IdentityService
import com.gu.identity.frontend.views.ViewRenderer._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

class ConsentController(
  configuration: Configuration,
  identityService: IdentityService,
  val messagesApi: MessagesApi,
  implicit val executionContext: ExecutionContext
) extends Controller
  with Logging
  with I18nSupport {

  def confirmConsents(consentToken: String) = Action.async {
    identityService.processConsentToken(consentToken).map {
      case Right(_) =>
        Redirect("/email-prefs", Map("consentsUpdated" -> Seq("true")))
      case Left(_) =>
        renderErrorPage(configuration, NotFoundError("The requested page was not found."), NotFound.apply)
    }.recover {
      case NonFatal(e) =>
        logger.error("Failed to process consent token", e)
        renderErrorPage(configuration, NotFoundError("The requested page was not found."), NotFound.apply)
    }
  }
}
