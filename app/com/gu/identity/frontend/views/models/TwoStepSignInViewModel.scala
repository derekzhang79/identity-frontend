package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.controllers.routes
import com.gu.identity.frontend.csrf.CSRFToken
import com.gu.identity.frontend.models._
import com.gu.identity.frontend.models.Text._
import com.gu.identity.frontend.mvt.ActiveMultiVariantTests
import com.gu.identity.model.{CurrentUser, GuestUser, NewUser, UserType}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat

case class TwoStepSignInViewModel private(
  layout: LayoutViewModel,

  oauth: OAuthSignInViewModel,

  twoStepSignInPageText: Map[String, String],
  terms: TermsViewModel,

  errors: Seq[ErrorViewModel] = Seq.empty,

  csrfToken: Option[CSRFToken],
  returnUrl: String = "",
  skipConfirmation: Boolean = false,
  clientId: Option[ClientID],
  group: Option[GroupCode],

  email:Option[String],
  emailForDisplay:Option[String],

  registerUrl: String = "",
  signinUrl: String = "",
  forgotPasswordUrl: String = "",

  userTypes: Map[String, Boolean],

  recaptchaModel: Option[Any],

  actions: Map[String, String] = Map(
    "signInWithEmailAndPassword" -> routes.SigninAction.signIn().url,
    "resetPassword" -> routes.ResetPasswordAction.reset().url,
    "signInWithEmail" -> routes.SigninAction.emailSignInFirstStep().url,
    "signInSecondStepCurrent" -> routes.SigninAction.signInSecondStepCurrent().url
  ),
  resources: Seq[PageResource with Product],
  indirectResources: Seq[PageResource with Product])
  extends ViewModel
    with ViewModelResources


object TwoStepSignInViewModel {
  def apply(
    configuration: Configuration,
    activeTests: ActiveMultiVariantTests,
    csrfToken: Option[CSRFToken],
    errors: Seq[ErrorViewModel],
    returnUrl: ReturnUrl,
    skipConfirmation: Option[Boolean],
    skipConsentJourney: Option[Boolean],
    clientId: Option[ClientID],
    group: Option[GroupCode],
    email: Option[String],
    userType: Option[UserType])(implicit messages: Messages): TwoStepSignInViewModel = {

    val layout = LayoutViewModel(configuration, activeTests, clientId, Some(returnUrl))
    val recaptchaModel : Option[GoogleRecaptchaViewModel] = None

    val resources = getResources(layout, recaptchaModel) ++ Seq(IndirectlyLoadedExternalResources(UrlBuilder(configuration.identityProfileBaseUrl,routes.SigninAction.signInWithSmartLock())))

    val isMembership = clientId.exists(_ == GuardianMembersClientID)

    TwoStepSignInViewModel(
      layout = layout,

      oauth = OAuthSignInViewModel(configuration, returnUrl, skipConfirmation, clientId, group, activeTests, skipConsentJourney),

      twoStepSignInPageText = TwoStepSignInPageText.toMap(isMembership),
      terms = Terms.getTermsModel(group),

      errors = errors,

      csrfToken = csrfToken,
      returnUrl = returnUrl.url,
      skipConfirmation = skipConfirmation.getOrElse(false),
      clientId = clientId,
      group = group,
      email = email,
      emailForDisplay = email.map(breakEmailWords),

      registerUrl = UrlBuilder(routes.Application.register(), returnUrl, skipConfirmation, clientId, group.map(_.id)),
      signinUrl = UrlBuilder(routes.Application.twoStepSignIn(), returnUrl, skipConfirmation, clientId, group.map(_.id)),
      forgotPasswordUrl = UrlBuilder("/reset", returnUrl, skipConfirmation, clientId, group.map(_.id)),

      userTypes = Map(
        ("isNew", userType.contains(NewUser)),
        ("isExisting", userType.contains(CurrentUser)),
        ("isGuest", userType.contains(GuestUser))
      ),

      recaptchaModel = recaptchaModel,

      resources = resources,
      indirectResources = layout.indirectResources
    )
  }

  private def breakEmailWords(email: String) = {
    HtmlFormat.escape(email).toString.flatMap {
      case '@' => s"<wbr>@"
      case '+' => s"<wbr>+"
      case c   => s"$c"
    }
  }

  private def getResources(layout: LayoutViewModel, recaptchaViewModel: Option[GoogleRecaptchaViewModel]): Seq[PageResource with Product] ={
    layout.resources
  }
}
