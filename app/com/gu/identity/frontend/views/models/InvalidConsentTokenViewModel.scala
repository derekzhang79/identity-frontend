package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.csrf.CSRFToken
import com.gu.identity.frontend.models.text.InvalidConsentTokenText
import play.api.i18n.Messages

case class InvalidConsentTokenViewModel private(
  layout: LayoutViewModel,
  token: String,
  csrfToken: Option[CSRFToken],
  errors: Seq[ErrorViewModel],
  text: InvalidConsentTokenText,
  resources: Seq[PageResource with Product],
  indirectResources: Seq[PageResource with Product]
)
  extends ViewModel
    with ViewModelResources

object InvalidConsentTokenViewModel {

  def apply(configuration: Configuration, token: String, csrfToken: Option[CSRFToken], errors: Seq[ErrorViewModel])(implicit messages: Messages): InvalidConsentTokenViewModel = {
    val layout = LayoutViewModel(configuration, token = Some(token))

    InvalidConsentTokenViewModel(
      layout = layout,
      token = token,
      csrfToken = csrfToken,
      errors = errors,
      text = InvalidConsentTokenText(),
      resources = layout.resources,
      indirectResources = layout.indirectResources
    )
  }
}
