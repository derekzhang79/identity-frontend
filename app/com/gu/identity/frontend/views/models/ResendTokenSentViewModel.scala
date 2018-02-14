package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.csrf.CSRFToken
import com.gu.identity.frontend.models.text.ResendTokenSentText
import play.api.i18n.Messages

case class ResendTokenSentViewModel private(
  layout: LayoutViewModel,
  csrfToken: Option[CSRFToken],
  errors: Option[Seq[ErrorViewModel]] = Some(Seq.empty),
  resendLinkEmailSentText: ResendTokenSentText,
  resources: Seq[PageResource with Product],
  indirectResources: Seq[PageResource with Product]
  )
  extends ViewModel
  with ViewModelResources

object ResendTokenSentViewModel {

  def apply(configuration: Configuration, csrfToken: Option[CSRFToken], errorIds: Option[Seq[ErrorViewModel]])(implicit messages: Messages): ResendTokenSentViewModel = {
    val layout = LayoutViewModel(configuration)

    ResendTokenSentViewModel(
      layout = layout,
      csrfToken = csrfToken,
      errorIds,
      resendLinkEmailSentText = ResendTokenSentText(),
      resources = layout.resources,
      indirectResources = layout.indirectResources
    )
  }
}
