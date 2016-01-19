package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.configuration.{MultiVariantTestVariant, MultiVariantTest, Configuration}
import com.gu.identity.frontend.controllers.routes
import com.gu.identity.frontend.models.{UrlBuilder, ReturnUrl}
import com.gu.identity.frontend.models.Text._
import play.api.i18n.Messages

case class SignInLinksViewModel private(
    socialFacebook: String,
    socialGoogle: String)
  extends ViewModel

object SignInLinksViewModel {
  def apply(urlParams: Seq[(String, String)]): SignInLinksViewModel =
    SignInLinksViewModel(
      socialFacebook = UrlBuilder("https://oauth.theguardian.com/facebook/signin", urlParams),
      socialGoogle = UrlBuilder("https://oauth.theguardian.com/google/signin", urlParams)
    )
}

case class SignInViewModel private(
    layout: LayoutViewModel,
    signInPageText: Map[String, String],

    oauth: OAuthSignInViewModel,

    showPrelude: Boolean = false,
    hasErrors: Boolean = false,
    errors: Seq[ErrorViewModel] = Seq.empty,
    returnUrl: String = "",
    skipConfirmation: Boolean = false,
    registerUrl: String = "",
    forgotPasswordUrl: String = "",
    links: SignInLinksViewModel,
    actions: Map[String, String] = Map("signIn" -> routes.SigninAction.signIn().url),
    resources: Seq[PageResource with Product],
    indirectResources: Seq[PageResource with Product])
  extends ViewModel
  with ViewModelResources


object SignInViewModel {
  def apply(configuration: Configuration, activeTests: Iterable[(MultiVariantTest, MultiVariantTestVariant)], errors: Seq[ErrorViewModel], returnUrl: ReturnUrl, skipConfirmation: Option[Boolean])(implicit messages: Messages): SignInViewModel = {
    val urlParams: Seq[(String, String)] = Seq(Some("returnUrl" -> returnUrl.url), skipConfirmation.map(bool => ("skipConfirmation", bool.toString))).flatten

    val layout = LayoutViewModel(configuration, activeTests)

    SignInViewModel(
      layout = layout,
      signInPageText = SignInPageText.toMap,

      oauth = OAuthSignInViewModel(returnUrl, skipConfirmation),

      hasErrors = errors.nonEmpty,
      errors = errors,
      returnUrl = returnUrl.url,
      skipConfirmation = skipConfirmation.getOrElse(false),
      registerUrl = UrlBuilder("/register", urlParams),
      forgotPasswordUrl = UrlBuilder("/reset", urlParams),
      links = SignInLinksViewModel(urlParams),
      resources = layout.resources,
      indirectResources = layout.indirectResources
    )
  }
}
