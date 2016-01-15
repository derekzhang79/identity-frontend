package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.configuration.{MultiVariantTestVariant, MultiVariantTest, Configuration}
import play.api.i18n.Messages


case class RegisterViewModel(
    layout: LayoutViewModel,

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
      resources = layout.resources,
      indirectResources = layout.indirectResources
    )
  }

}
