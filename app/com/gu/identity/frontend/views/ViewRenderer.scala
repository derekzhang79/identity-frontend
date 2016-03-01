package com.gu.identity.frontend.views

import com.gu.identity.frontend.configuration._
import com.gu.identity.frontend.csrf.CSRFToken
import com.gu.identity.frontend.errors.HttpError
import com.gu.identity.frontend.models.{ClientID, ReturnUrl}
import com.gu.identity.frontend.mvt.{MultiVariantTestVariant, MultiVariantTest}
import com.gu.identity.frontend.views.models._
import jp.co.bizreach.play2handlebars.HBS
import play.api.i18n.Messages
import play.api.mvc.{Result, Results}
import play.twirl.api.Html

/**
 * Adapter for Handlebars view renderer
 */
object ViewRenderer {
  def render(view: String, attributes: Map[String, Any] = Map.empty) =
    HBS(view, attributes)

  def renderSignIn(
      configuration: Configuration,
      activeTests: Map[MultiVariantTest, MultiVariantTestVariant],
      csrfToken: Option[CSRFToken],
      errorIds: Seq[String],
      returnUrl: ReturnUrl,
      skipConfirmation: Option[Boolean],
      clientId: Option[ClientID])
      (implicit messages: Messages) = {

    val model = SignInViewModel(
      configuration = configuration,
      activeTests = activeTests,
      csrfToken = csrfToken,
      errors = errorIds.map(ErrorViewModel.apply),
      returnUrl = returnUrl,
      skipConfirmation = skipConfirmation,
      clientId = clientId
    )

    val view = "signin-page"

    renderViewModel(view, model)
  }

  def renderRegister(
      configuration: Configuration,
      activeTests: Map[MultiVariantTest, MultiVariantTestVariant],
      errorIds: Seq[String],
      csrfToken: Option[CSRFToken],
      returnUrl: ReturnUrl,
      skipConfirmation: Option[Boolean],
      clientId: Option[ClientID])
      (implicit messages: Messages) = {

    val model = RegisterViewModel(
      configuration = configuration,
      activeTests = activeTests,
      errors = errorIds.map(ErrorViewModel.apply),
      csrfToken = csrfToken,
      returnUrl = returnUrl,
      skipConfirmation = skipConfirmation,
      clientId = clientId)

    renderViewModel("register-page", model)
  }

  def renderRegisterConfirmation(configuration: Configuration, returnUrl: ReturnUrl, clientId: Option[ClientID])(implicit messages: Messages) = {
    renderViewModel(
      "register-confirmation-page",
      RegisterConfirmationViewModel(configuration, returnUrl, clientId))
  }

  def renderResetPassword(
    configuration: Configuration,
    errorIds: Seq[String],
    csrfToken: Option[CSRFToken],
    clientId: Option[ClientID])
    (implicit messages: Messages) = {
    val model = ResetPasswordViewModel(
      configuration = configuration,
      errors = errorIds.map(ErrorViewModel.apply),
      csrfToken = csrfToken,
      clientId = clientId
    )
    renderViewModel("reset-password-page", model)
  }

  def renderResetPasswordEmailSent(configuration: Configuration, clientId: Option[ClientID])(implicit messages: Messages) = {
    val model = ResetPasswordEmailSentViewModel(
      configuration = configuration,
      clientId = clientId
    )
    renderViewModel("reset-password-email-sent-page", model)
  }


  def renderErrorPage(configuration: Configuration, error: HttpError, resultGenerator: Html => Result)(implicit messages: Messages) =
    renderViewModel("error-page", ErrorPageViewModel(configuration, error), resultGenerator)


  def renderViewModel(
      view: String,
      model: ViewModel with ViewModelResources with Product,
      resultGenerator: Html => Result = Results.Ok.apply): Result = {

    val html = HBS.withProduct(view, model)

    resultGenerator(html)
      .withHeaders(ContentSecurityPolicy.cspForViewModel(model))
  }

}
