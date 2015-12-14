package com.gu.identity.frontend.controllers

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.logging.Logging
import com.gu.identity.frontend.views.ViewRenderer.renderSignIn
import play.api.i18n.{MessagesApi, I18nSupport}
import play.api.mvc._
import play.api.i18n.Messages.Implicits._

class Application (configuration: Configuration, val messagesApi: MessagesApi) extends Controller with Logging with I18nSupport {

  def index = Action {
    Redirect(routes.Application.signIn(email = None, error = Seq.empty))
  }

  def signIn(email: Option[String], error: Seq[String]) = Action { implicit req =>
    Cached{
      Ok(renderSignIn(configuration, error, email.getOrElse(""))(messagesApi))
    }
  }
}
