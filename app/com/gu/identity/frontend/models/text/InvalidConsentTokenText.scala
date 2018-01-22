package com.gu.identity.frontend.models.text

import play.api.i18n.Messages

case class InvalidConsentTokenText private(
  pageTitle: String,
  title: String,
  description: String
)

object InvalidConsentTokenText {
  def apply()(implicit messages: Messages): InvalidConsentTokenText =
    InvalidConsentTokenText(
      pageTitle = messages("resendConsentLink.pageTitle"),
      title = messages("resendConsentLink.title"),
      description = messages("resendConsentLink.description")
    )
}
