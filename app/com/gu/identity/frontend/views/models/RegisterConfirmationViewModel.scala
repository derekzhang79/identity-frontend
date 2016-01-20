package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.configuration.Configuration
import play.api.i18n.Messages

case class RegisterConfirmationViewModel private(
    layout: LayoutViewModel,
    returnUrl: String,
    resources: Seq[PageResource with Product],
    indirectResources: Seq[PageResource with Product])
  extends ViewModel
  with ViewModelResources

object RegisterConfirmationViewModel {
  def apply(configuration: Configuration, returnUrl: Option[String])(implicit messages: Messages): RegisterConfirmationViewModel = {
    val layout = LayoutViewModel(configuration, None)

    RegisterConfirmationViewModel(
      layout = layout,
      returnUrl = returnUrl.getOrElse("http://www.theguardian.com"),
      resources = layout.resources,
      indirectResources = layout.indirectResources
    )
  }
}
