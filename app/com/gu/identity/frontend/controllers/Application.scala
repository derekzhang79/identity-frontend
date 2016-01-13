package com.gu.identity.frontend.controllers

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.logging.Logging
import com.gu.identity.frontend.models.ReturnUrl
import com.gu.identity.frontend.views.ViewRenderer.renderSignIn
import play.api.i18n.{MessagesApi, I18nSupport}
import play.api.mvc._

class Application (configuration: Configuration, val messagesApi: MessagesApi) extends Controller with Logging with I18nSupport {

  def index = Action {
    Redirect(routes.Application.signIn())
  }

  def signIn(error: Seq[String], returnUrl: Option[String], skipConfirmation: Option[Boolean]) = Action { implicit req =>
    Cached{
      Ok(renderSignIn(configuration, error, returnUrl, skipConfirmation))
    }
  }

  def register(error: Seq[String], returnUrl: Option[String], skipConfirmation: Option[Boolean], group: Option[String]) = Action {
    Cached{
      Ok("Hello, This is the Registration Page")
    }
  }

  def confirm(returnUrl: String) = Action {
    Cached{
      Ok("Hello, This is the Registration Confirmation Page")
    }
  }
}
