package com.gu.identity.frontend.controllers

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.csrf.{CSRFAddToken, CSRFConfig, CSRFToken}
import com.gu.identity.frontend.logging.Logging
import com.gu.identity.frontend.models.{ClientID, GroupCode, ReturnUrl}
import com.gu.identity.frontend.mvt.MultiVariantTestAction
import com.gu.identity.frontend.views.ViewRenderer._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._


class Application (configuration: Configuration, val messagesApi: MessagesApi, csrfConfig: CSRFConfig) extends Controller with Logging with I18nSupport {

  def signIn(error: Seq[String], returnUrl: Option[String], skipConfirmation: Option[Boolean], clientId: Option[String], group: Option[String]) = (CSRFAddToken(csrfConfig) andThen MultiVariantTestAction) { req =>
    val clientIdActual = ClientID(clientId)
    val returnUrlActual = ReturnUrl(returnUrl, req.headers.get("Referer"), configuration, clientIdActual)
    val csrfToken = CSRFToken.fromRequest(csrfConfig, req)
    val groupCode = GroupCode(group)
    val email : Option[String] = req.getQueryString("email")

    renderSignIn(configuration, req.activeTests, csrfToken, error, returnUrlActual, skipConfirmation, clientIdActual, groupCode, email)
  }

  def register(error: Seq[String], returnUrl: Option[String], skipConfirmation: Option[Boolean],  clientId: Option[String], group: Option[String]) = (CSRFAddToken(csrfConfig) andThen MultiVariantTestAction) { req =>
    val clientIdActual = ClientID(clientId)
    val returnUrlActual = ReturnUrl(returnUrl, req.headers.get("Referer"), configuration, clientIdActual)
    val csrfToken = CSRFToken.fromRequest(csrfConfig, req)
    val groupCode = GroupCode(group)
    val email : Option[String] = req.getQueryString("email")

    renderRegister(configuration, req.activeTests, error, csrfToken, returnUrlActual, skipConfirmation, clientIdActual, groupCode, email)
  }

  def confirm(returnUrl: Option[String], clientId: Option[String]) = Action {
    val clientIdOpt = ClientID(clientId)
    val returnUrlActual = ReturnUrl(returnUrl, refererHeader = None, configuration, clientIdOpt)

    renderRegisterConfirmation(configuration, returnUrlActual, clientIdOpt)
  }

  def reset(error: Seq[String], clientId: Option[String]) = CSRFAddToken(csrfConfig) { req =>
    val clientIdOpt = ClientID(clientId)
    val csrfToken = CSRFToken.fromRequest(csrfConfig, req)

    renderResetPassword(configuration, error, csrfToken, clientIdOpt)
  }

  def resetPasswordEmailSent(clientId: Option[String]) = Action {
    val clientIdOpt = ClientID(clientId)
    renderResetPasswordEmailSent(configuration, clientIdOpt)
  }
}
