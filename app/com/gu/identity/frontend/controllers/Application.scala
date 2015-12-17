package com.gu.identity.frontend.controllers

import javax.inject.Inject

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.logging.Logging
import com.gu.identity.frontend.views.ViewRenderer.renderSignIn
import play.api.i18n.{MessagesApi, I18nSupport}
import play.api.mvc._

class Application (configuration: Configuration, val messagesApi: MessagesApi) extends Controller with Logging with I18nSupport {

  def index = Action {
    Redirect(routes.Application.signIn(error = Seq.empty, returnUrl = None, skipConfirmation = None))
  }

  def signIn(error: Seq[String], returnUrl: Option[String], skipConfirmation: Option[Boolean]) = Action { implicit req =>
    Cached{
      Ok(renderSignIn(configuration, error, returnUrl, skipConfirmation))
    }
  }
}
