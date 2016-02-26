package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.models.text.ResetPasswordEmailSentText
import play.api.i18n.Messages

case class ResetPasswordEmailSentViewModel private(
                                           layout: LayoutViewModel,
                                           resetPasswordEmailSentText: ResetPasswordEmailSentText,
                                           resources: Seq[PageResource with Product],
                                           indirectResources: Seq[PageResource with Product]
                                           )
  extends ViewModel
  with ViewModelResources

object ResetPasswordEmailSentViewModel {

  def apply(configuration: Configuration)(implicit messages: Messages): ResetPasswordEmailSentViewModel = {
    val layout = LayoutViewModel(configuration)

    ResetPasswordEmailSentViewModel(
      layout = layout,
      resetPasswordEmailSentText = ResetPasswordEmailSentText(),
      resources = layout.resources,
      indirectResources = layout.indirectResources
    )
  }
}

