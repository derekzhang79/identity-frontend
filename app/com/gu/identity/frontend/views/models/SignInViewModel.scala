package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.configuration.{MultiVariantTestVariant, MultiVariantTest, Configuration}
import com.gu.identity.frontend.controllers.routes
import com.gu.identity.frontend.models.{UrlBuilder, ReturnUrl}
import com.gu.identity.frontend.models.Text._
import play.api.i18n.Messages

case class SignInViewModel private(
    layout: LayoutViewModel,

    oauth: OAuthSignInViewModel,

    signInPageText: Map[String, String],
    terms: TermsViewModel,

    showPrelude: Boolean = false,
    hasErrors: Boolean = false,
    errors: Seq[ErrorViewModel] = Seq.empty,
    returnUrl: String = "",
    skipConfirmation: Boolean = false,
    registerUrl: String = "",
    forgotPasswordUrl: String = "",

    actions: Map[String, String] = Map("signIn" -> routes.SigninAction.signIn().url),
    resources: Seq[PageResource with Product],
    indirectResources: Seq[PageResource with Product])
  extends ViewModel
  with ViewModelResources


object SignInViewModel {
  def apply(configuration: Configuration, activeTests: Iterable[(MultiVariantTest, MultiVariantTestVariant)], errors: Seq[ErrorViewModel], returnUrl: ReturnUrl, skipConfirmation: Option[Boolean])(implicit messages: Messages): SignInViewModel = {
    val layout = LayoutViewModel(configuration, activeTests)

    SignInViewModel(
      layout = layout,

      oauth = OAuthSignInViewModel(returnUrl, skipConfirmation),

      signInPageText = SignInPageText.toMap,
      terms = TermsViewModel(),

      hasErrors = errors.nonEmpty,
      errors = errors,
      returnUrl = returnUrl.url,
      skipConfirmation = skipConfirmation.getOrElse(false),
      registerUrl = UrlBuilder(routes.Application.register(), returnUrl, skipConfirmation),
      forgotPasswordUrl = UrlBuilder("/reset", returnUrl, skipConfirmation),

      resources = layout.resources,
      indirectResources = layout.indirectResources
    )
  }
}
