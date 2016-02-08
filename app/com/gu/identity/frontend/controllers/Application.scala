package com.gu.identity.frontend.controllers

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.csrf.{CSRFConfig, CSRFToken, CSRFAddToken}
import com.gu.identity.frontend.logging.Logging
import com.gu.identity.frontend.models.{ClientID, ReturnUrl}
import com.gu.identity.frontend.mvt.MultiVariantTestAction
import com.gu.identity.frontend.views.ViewRenderer.{renderSignIn, renderRegisterConfirmation, renderRegister}
import play.api.i18n.{MessagesApi, I18nSupport}
import play.api.mvc._


class Application (configuration: Configuration, val messagesApi: MessagesApi, csrfConfig: CSRFConfig) extends Controller with Logging with I18nSupport {

  def index = Action {
    Redirect(routes.Application.signIn())
  }

  def signIn(error: Seq[String], returnUrl: Option[String], skipConfirmation: Option[Boolean], clientId: Option[String]) = (CSRFAddToken(csrfConfig) andThen MultiVariantTestAction) { req =>
    val returnUrlActual = ReturnUrl(returnUrl, req.headers.get("Referer"), configuration)
    val clientIdActual = ClientID(clientId)

    val csrfToken = CSRFToken.fromRequest(csrfConfig, req)

    renderSignIn(configuration, req.activeTests, csrfToken, error, returnUrlActual, skipConfirmation, clientIdActual)
  }

  def register(error: Seq[String], returnUrl: Option[String], skipConfirmation: Option[Boolean], group: Option[String], clientId: Option[String]) = (CSRFAddToken(csrfConfig) andThen MultiVariantTestAction) { req =>
    val returnUrlActual = ReturnUrl(returnUrl, req.headers.get("Referer"), configuration)
    val clientIdActual = ClientID(clientId)

    val csrfToken = CSRFToken.fromRequest(csrfConfig, req)

    renderRegister(configuration, req.activeTests, error, csrfToken, returnUrlActual, skipConfirmation, clientIdActual)
  }

  def confirm(returnUrl: Option[String]) = Action { req =>
    val returnUrlActual = ReturnUrl(returnUrl, req.headers.get("Referer"), configuration)
    renderRegisterConfirmation(configuration, returnUrlActual)
  }
}

