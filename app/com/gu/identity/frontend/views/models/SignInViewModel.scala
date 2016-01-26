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

    recaptchaEnabled: Boolean,
    recaptchaSiteKey: String,

    actions: Map[String, String] = Map("signIn" -> routes.SigninAction.signIn().url),
    resources: Seq[PageResource with Product],
    indirectResources: Seq[PageResource with Product])
  extends ViewModel
  with ViewModelResources


object SignInViewModel {
  def apply(configuration: Configuration, activeTests: Iterable[(MultiVariantTest, MultiVariantTestVariant)], errors: Seq[ErrorViewModel], returnUrl: ReturnUrl, skipConfirmation: Option[Boolean])(implicit messages: Messages): SignInViewModel = {

    val layout = LayoutViewModel(configuration, activeTests)

    if(errors.isEmpty) {
      val resources = layout.resources
      createSignInViewModel(layout, configuration, returnUrl, skipConfirmation, errors, resources)
    } else {
      val recaptchaResources = GoogleRecaptchaResources(configuration)
      val resources = layout.resources ++ recaptchaResources.resources
      val recaptchaSiteKey = Some(recaptchaResources.googleSiteKey)
      createSignInViewModel(layout, configuration, returnUrl, skipConfirmation, errors, resources, recaptchaEnabled = true, recaptchaSiteKey)
    }
  }

  private def createSignInViewModel(
       layout: LayoutViewModel,
       configuration: Configuration,
       returnUrl: ReturnUrl,
       skipConfirmation: Option[Boolean],
       errors: Seq[ErrorViewModel],
       resources: Seq[PageResource with Product],
       recaptchaEnabled: Boolean = false,
       recaptchaSiteKey: Option[String] = None)(implicit messages: Messages): SignInViewModel = {

    SignInViewModel(
      layout = layout,

      oauth = OAuthSignInViewModel(configuration, returnUrl, skipConfirmation),

      signInPageText = SignInPageText.toMap,
      terms = TermsViewModel(),

      hasErrors = errors.nonEmpty,
      errors = errors,
      returnUrl = returnUrl.url,
      skipConfirmation = skipConfirmation.getOrElse(false),
      registerUrl = UrlBuilder(routes.Application.register(), returnUrl, skipConfirmation),
      forgotPasswordUrl = UrlBuilder("/reset", returnUrl, skipConfirmation),

      recaptchaEnabled = recaptchaEnabled,
      recaptchaSiteKey = recaptchaSiteKey.getOrElse(""),

      resources = resources,
      indirectResources = layout.indirectResources
    )
  }
}

case class GoogleRecaptchaResources(
   resources: Seq[PageResource with Product] = Seq(
     JavascriptResource("https://www.google.com/recaptcha/api.js", "https://www.google.com", isInHead = true),
     IndirectlyLoadedExternalScriptResources("https://www.gstatic.com"),
     IndirectlyLoadedExternalFrameResource("https://www.google.com"),
     UnsafeInlineCSSResource),

   googleSiteKey: String
   )

object GoogleRecaptchaResources {

  def apply(configuration: Configuration): GoogleRecaptchaResources = {
    GoogleRecaptchaResources(
      googleSiteKey = configuration.googleRecaptchaSiteKey
    )
  }
}
