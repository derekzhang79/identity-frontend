package com.gu.identity.frontend.models.text

import play.api.i18n.Messages

case class ResendConsentTokenSentText private(
  pageTitle: String,
  title: String,
  description: String,
  info: String
)

object ResendConsentTokenSentText {
  def apply()(implicit messages: Messages): ResendConsentTokenSentText =
    ResendConsentTokenSentText(
      pageTitle = messages("resendConsentTokenSent.pageTitle"),
      title = messages("resendConsentTokenSent.title"),
      description = messages("resendConsentTokenSent.description"),
      info = messages("resendConsentTokenSent.info", "/email-prefs")
    )
}
