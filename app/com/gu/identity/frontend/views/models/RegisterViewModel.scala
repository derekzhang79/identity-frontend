package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.configuration.{MultiVariantTestVariant, MultiVariantTest, Configuration}
import com.gu.identity.frontend.models.text.RegisterText
import play.api.i18n.Messages


case class RegisterViewModel(
    layout: LayoutViewModel,

    socialSignInText: Map[String, String] = Map(
      "facebook" -> "Sign up with Facebook",
      "google" -> "Sign up with Google"
    ),

    registerPageText: RegisterText,
    terms: TermsViewModel,

    actions: Map[String, String] = Map("register" -> "/actions/register"),

    resources: Seq[PageResource with Product],
    indirectResources: Seq[PageResource with Product])
  extends ViewModel with ViewModelResources


object RegisterViewModel {

  def apply(
      configuration: Configuration,
      activeTests: Iterable[(MultiVariantTest, MultiVariantTestVariant)])
      (implicit messages: Messages): RegisterViewModel = {

    val layout = LayoutViewModel(configuration, activeTests)

    RegisterViewModel(
      layout = layout,

      registerPageText = RegisterText(),
      terms = TermsViewModel(),

      resources = layout.resources,
      indirectResources = layout.indirectResources
    )
  }

}
