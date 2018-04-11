package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.controllers.routes
import com.gu.identity.frontend.csrf.CSRFToken
import com.gu.identity.frontend.models._
import com.gu.identity.frontend.models.Text._
import com.gu.identity.frontend.mvt.ActiveMultiVariantTests
import play.api.i18n.Messages

case class SignInViewModel private(
    layout: LayoutViewModel,

    oauth: OAuthSignInViewModel,

    signInPageText: Map[String, String],
    terms: TermsViewModel,

    showPrelude: Boolean = false,
    hasErrors: Boolean = false,
    errors: Seq[ErrorViewModel] = Seq.empty,

    csrfToken: Option[CSRFToken],
    returnUrl: String = "",
    skipConfirmation: Boolean = false,
    clientId: Option[ClientID],
    group: Option[GroupCode],
    email:Option[String],

    registerUrl: String = "",
    forgotPasswordUrl: String = "",

    recaptchaModel: Option[GoogleRecaptchaViewModel],

    actions: Map[String, String] = Map("signIn" -> routes.SigninAction.signIn().url),
    resources: Seq[PageResource with Product],
    indirectResources: Seq[PageResource with Product])
  extends ViewModel
  with ViewModelResources


object SignInViewModel {
  def apply(
   configuration: Configuration,
   activeTests: ActiveMultiVariantTests,
   csrfToken: Option[CSRFToken],
   errors: Seq[ErrorViewModel],
   returnUrl: ReturnUrl,
   skipConfirmation: Option[Boolean],
   clientId: Option[ClientID],
   group: Option[GroupCode],
   email: Option[String],
   skipConsentJourney: Option[Boolean])(implicit messages: Messages): SignInViewModel = {

    val layout = LayoutViewModel(configuration, activeTests, clientId, Some(returnUrl))
    val recaptchaModel : Option[GoogleRecaptchaViewModel] =
      getRecaptchaModel(configuration, isError = errors.nonEmpty, recaptchaEnabled = configuration.recaptchaEnabled)

    val resources = getResources(layout, recaptchaModel) ++ Seq(IndirectlyLoadedExternalResources(UrlBuilder(configuration.identityProfileBaseUrl,routes.SigninAction.signInWithSmartLock())))

    val isMembership = clientId.exists(_ == GuardianMembersClientID)

    SignInViewModel(
      layout = layout,

      oauth = OAuthSignInViewModel(configuration, returnUrl, skipConfirmation, clientId, group, activeTests, skipConsentJourney),

      signInPageText = SignInPageText.toMap(isMembership),
      terms = Terms.getTermsModel(group),

      hasErrors = errors.nonEmpty,
      errors = errors,

      csrfToken = csrfToken,
      returnUrl = returnUrl.url,
      skipConfirmation = skipConfirmation.getOrElse(false),
      clientId = clientId,
      group = group,
      email = email,

      registerUrl = UrlBuilder(routes.Application.register(), returnUrl, skipConfirmation, clientId, group.map(_.id)),
      forgotPasswordUrl = UrlBuilder("/reset", returnUrl, skipConfirmation, clientId, group.map(_.id)),

      recaptchaModel = recaptchaModel,

      resources = resources,
      indirectResources = layout.indirectResources
    )
  }

  private def getRecaptchaModel(configuration: Configuration, isError: Boolean, recaptchaEnabled: Boolean): Option[GoogleRecaptchaViewModel] = {
    if(isError && recaptchaEnabled) {
      Some(GoogleRecaptchaViewModel(configuration))
    } else {
      None
    }
  }

  private def getResources(layout: LayoutViewModel, recaptchaViewModel: Option[GoogleRecaptchaViewModel]): Seq[PageResource with Product] ={
    recaptchaViewModel match {
      case Some(model) =>  layout.resources ++ model.resources
      case None => layout.resources
    }
  }
}

case class GoogleRecaptchaViewModel(
   resources: Seq[PageResource with Product] = Seq(
     JavascriptResource("https://www.google.com/recaptcha/api.js", "https://www.google.com", isInHead = true),
     IndirectlyLoadedExternalScriptResources("https://www.gstatic.com"),
     IndirectlyLoadedExternalFrameResource("https://www.google.com"),
     UnsafeInlineCSSResource),

   googleSiteKey: String
   )

object GoogleRecaptchaViewModel {
  def apply(configuration: Configuration): GoogleRecaptchaViewModel = {
    GoogleRecaptchaViewModel(
      googleSiteKey = configuration.googleRecaptchaSiteKey
    )
  }
}
