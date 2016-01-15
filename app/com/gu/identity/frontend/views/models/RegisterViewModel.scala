package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.configuration.{MultiVariantTestVariant, MultiVariantTest, Configuration}
import play.api.i18n.Messages


case class RegisterViewModel(
    layout: LayoutViewModel,

    socialSignInText: Map[String, String] = Map(
      "facebook" -> "Sign up with Facebook",
      "google" -> "Sign up with Google"
    ),

    registerPageText: Map[String, String],

    terms: Map[String, String] = Map(
      "conditionsText" -> "By proceeding, you agree to the Guardian's",
      "termsOfServiceText" -> "Terms of Service",
      "privacyPolicyText" -> "Privacy Policy"
    ),

    actions: Map[String, String] = Map("register" -> "/actions/register"),

    resources: Seq[PageResource with Product],
    indirectResources: Seq[PageResource with Product])
  extends ViewModel with ViewModelResources


object RegisterViewModel {

  val globalTextKeys = Seq("signIn", "sign")

  val registerTextKeys = Seq(
    "divideText", "name", "firstName", "lastName", "username", "usernameHelp",
    "email", "emailHelp", "password", "passwordHelp", "gnmMarketing",
    "3rdPartyMarketing", "signInCta", "signIn", "createAccount"
  )

  def apply(
      configuration: Configuration,
      activeTests: Iterable[(MultiVariantTest, MultiVariantTestVariant)])
      (implicit messages: Messages): RegisterViewModel = {

    val layout = LayoutViewModel(configuration, activeTests)
    val registerPageText = registerTextKeys.map(k => k -> messages(s"register.$k")).toMap

    RegisterViewModel(
      layout = layout,

      registerPageText = registerPageText,

      resources = layout.resources,
      indirectResources = layout.indirectResources
    )
  }

}
