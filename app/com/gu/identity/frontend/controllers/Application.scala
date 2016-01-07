package com.gu.identity.frontend.controllers

import com.gu.identity.frontend.configuration.{MultiVariantTests, Configuration}
import com.gu.identity.frontend.logging.Logging
import com.gu.identity.frontend.models.ReturnUrl
import com.gu.identity.frontend.views.ViewRenderer.renderSignIn
import play.api.i18n.{MessagesApi, I18nSupport}
import play.api.mvc._

import scala.util.Try

class Application (configuration: Configuration, val messagesApi: MessagesApi) extends Controller with Logging with I18nSupport {

  def index = Action {
    Redirect(routes.Application.signIn())
  }

  def signIn(error: Seq[String], returnUrl: Option[String], skipConfirmation: Option[Boolean]) = Action { req =>
    Cached {
      val returnUrlActual = ReturnUrl(returnUrl, req.headers.get("Referer"))
      val mvtCookie = getMvtCookie(req)
      val activeTests = mvtCookie.map(id => MultiVariantTests.activeTests(id)).getOrElse(Nil)

      Ok(renderSignIn(configuration, activeTests, error, returnUrlActual, skipConfirmation))
    }
  }

  def register(error: Seq[String], returnUrl: Option[String]) = Action {
    Cached{
      Ok("Hello, This is the Registration Page")
    }
  }

  private def getMvtCookie(req: RequestHeader) =
    req.cookies.get(MultiVariantTests.MVT_COOKIE_NAME).flatMap { mvtId =>
      Try(Integer.parseInt(mvtId.value)).toOption
    }
}
