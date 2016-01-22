package com.gu.identity.frontend.controllers

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.csrf.{CSRFToken, CSRFAddToken}
import com.gu.identity.frontend.logging.Logging
import com.gu.identity.frontend.models.ReturnUrl
import com.gu.identity.frontend.views.ViewRenderer.{renderSignIn, renderRegisterConfirmation, renderRegister}
import play.api.i18n.{MessagesApi, I18nSupport}
import play.api.mvc._
import play.filters.csrf.CSRFConfig


class Application (configuration: Configuration, val messagesApi: MessagesApi, csrfConfig: CSRFConfig) extends Controller with Logging with I18nSupport {

  def index = Action {
    Redirect(routes.Application.signIn())
  }

  def signIn(error: Seq[String], returnUrl: Option[String], skipConfirmation: Option[Boolean]) = MultiVariantTestAction { req =>
    val returnUrlActual = ReturnUrl(returnUrl, req.headers.get("Referer"))

    renderSignIn(configuration, req.activeTests, error, returnUrlActual, skipConfirmation)
  }

  def register(error: Seq[String], returnUrl: Option[String], skipConfirmation: Option[Boolean], group: Option[String]) = (CSRFAddToken(csrfConfig) andThen MultiVariantTestAction) { req =>
    val returnUrlActual = ReturnUrl(returnUrl, req.headers.get("Referer"))

    val csrfToken = CSRFToken.fromRequest(csrfConfig, req)

    renderRegister(configuration, req.activeTests, error, csrfToken, returnUrlActual, skipConfirmation)
  }

  def confirm(returnUrl: Option[String]) = Action {
    renderRegisterConfirmation(configuration, returnUrl)
  }
}

