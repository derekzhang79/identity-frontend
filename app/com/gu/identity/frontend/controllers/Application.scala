package com.gu.identity.frontend.controllers

import javax.inject.Inject

import com.gu.identity.frontend.logging.Logging
import com.gu.identity.frontend.views.ViewRenderer.renderSignIn
import play.api.mvc._

class Application @Inject() extends Controller with Logging {

  def index = Action {
    Redirect(routes.Application.signIn())
  }

  def signIn = Action { req =>
    Cached{
      Ok(renderSignIn(req.getQueryString("error").seq.toSeq, req.getQueryString("email").getOrElse("")))
    }
  }
}
