package com.gu.identity.frontend.views

import java.net.URI

import com.gu.identity.frontend.configuration._
import com.gu.identity.frontend.csrf.CSRFToken
import com.gu.identity.frontend.errors.{HttpError, NotFoundError, UnexpectedError}
import com.gu.identity.frontend.models._
import com.gu.identity.frontend.mvt.{MultiVariantTest, MultiVariantTestVariant}
import com.gu.identity.frontend.views.models._
import jp.co.bizreach.play2handlebars.HBS
import org.omg.CosNaming.NamingContextPackage.NotFound
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
      clientId: Option[ClientID],
      group: Option[GroupCode],
      email: Option[String])
      (implicit messages: Messages) = {

    val model = SignInViewModel(
      configuration = configuration,
      activeTests = activeTests,
      csrfToken = csrfToken,
      errors = errorIds.map(ErrorViewModel.apply),
      returnUrl = returnUrl,
      skipConfirmation = skipConfirmation,
      clientId = clientId,
      group = group,
      email = email
    )

    val view = clientId match {
      case Some(GuardianMembersClientID) => "signin-page"
      case _ => "signin-page"
    }

    renderViewModel(view, model)
  }

  def renderRegister(
      configuration: Configuration,
      activeTests: Map[MultiVariantTest, MultiVariantTestVariant],
      errorIds: Seq[String],
      csrfToken: Option[CSRFToken],
      returnUrl: ReturnUrl,
      skipConfirmation: Option[Boolean],
      clientId: Option[ClientID],
      group: Option[GroupCode],
      email: Option[String],
      shouldCollectConsents: Boolean,
      shouldCollectV2Consents: Boolean)
      (implicit messages: Messages) = {

    val model = RegisterViewModel(
      configuration = configuration,
      activeTests = activeTests,
      errors = errorIds,
      csrfToken = csrfToken,
      returnUrl = returnUrl,
      skipConfirmation = skipConfirmation,
      clientId = clientId,
      group = group,
      email = email,
      shouldCollectConsents = shouldCollectConsents,
      shouldCollectV2Consents = shouldCollectV2Consents
    )

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

  def renderInvalidConsentToken(configuration: Configuration, token: String, csrfToken: Option[CSRFToken], errorIds: Seq[String])(implicit messages: Messages) = {
    val model = InvalidConsentTokenViewModel(
      configuration = configuration,
      token = token,
      csrfToken = csrfToken,
      errors = errorIds.map(ErrorViewModel.apply)
    )
    renderViewModel("invalid-consent-token-page", model)
  }

  def renderResendConsentTokenSent(configuration: Configuration, csrfToken: Option[CSRFToken], errorIds: Seq[String])(implicit messages: Messages) = {
    val errors = errorIds.map(ErrorViewModel.apply)
    val maybeErrors = if(errors.isEmpty) None else Some(errors)

    val model = ResendConsentTokenSentViewModel(
      configuration = configuration,
      csrfToken = csrfToken,
      errorIds = maybeErrors
    )
    renderViewModel("resend-consent-link-sent-page", model)
  }


  def renderErrorPage(configuration: Configuration, error: HttpError, resultGenerator: Html => Result)(implicit messages: Messages) =
    renderViewModel("error-page", ErrorPageViewModel(configuration, error), resultGenerator)

  def renderTsAndCs(configuration: Configuration, clientId: Option[ClientID], group: GroupCode, returnUrl: ReturnUrl, signOutLink: URI)(implicit messages: Messages) = {
    val model = TsAndCsViewModel(configuration, clientId, group, returnUrl, signOutLink)
    renderViewModel("third-party-ts-and-cs-page", model)
  }

  def renderViewModel(
      view: String,
      model: ViewModel with ViewModelResources with Product,
      resultGenerator: Html => Result = Results.Ok.apply): Result = {

    val html = HBS.withProduct(view, model)

    resultGenerator(html)
      .withHeaders(ContentSecurityPolicy.cspForViewModel(model))
  }

}
