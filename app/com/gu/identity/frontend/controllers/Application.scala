package com.gu.identity.frontend.controllers

import javax.inject.Inject

import com.gu.identity.frontend.logging.Logging
import com.gu.identity.frontend.views.ViewRenderer.renderSignIn
import play.api.mvc._

class Application @Inject() extends Controller with Logging {

  def index = Action {
    Redirect(routes.Application.signIn(email = None))
  }

  def signIn(email: Option[String]) = Action { req =>
    Cached{
      Ok(renderSignIn(req.getQueryString("error").seq.toSeq, email.getOrElse("")))
    }
  }
}
