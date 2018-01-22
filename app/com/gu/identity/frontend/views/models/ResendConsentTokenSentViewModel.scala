package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.csrf.CSRFToken
import com.gu.identity.frontend.models.text.ResendConsentTokenSentText
import play.api.i18n.Messages

case class ResendConsentTokenSentViewModel private(
  layout: LayoutViewModel,
  csrfToken: Option[CSRFToken],
  errors: Option[Seq[ErrorViewModel]] = Some(Seq.empty),
  resendConsentLinkEmailSentText: ResendConsentTokenSentText,
  resources: Seq[PageResource with Product],
  indirectResources: Seq[PageResource with Product]
  )
  extends ViewModel
  with ViewModelResources

object ResendConsentTokenSentViewModel {

  def apply(configuration: Configuration, csrfToken: Option[CSRFToken], errorIds: Option[Seq[ErrorViewModel]])(implicit messages: Messages): ResendConsentTokenSentViewModel = {
    val layout = LayoutViewModel(configuration)

    ResendConsentTokenSentViewModel(
      layout = layout,
      csrfToken = csrfToken,
      errorIds,
      resendConsentLinkEmailSentText = ResendConsentTokenSentText(),
      resources = layout.resources,
      indirectResources = layout.indirectResources
    )
  }
}
