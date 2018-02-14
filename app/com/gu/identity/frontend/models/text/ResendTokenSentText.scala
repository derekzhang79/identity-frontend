package com.gu.identity.frontend.models.text

import play.api.i18n.Messages

case class ResendTokenSentText private(
  pageTitle: String,
  title: String,
  description: String,
  unexpectedTitle: String)

object ResendTokenSentText {
  def apply()(implicit messages: Messages): ResendTokenSentText =
    ResendTokenSentText(
      pageTitle = messages("resendTokenSent.pageTitle"),
      title = messages("resendTokenSent.title"),
      description = messages("resendTokenSent.description"),
      unexpectedTitle = messages("resendTokenSent.unexpectedTitle")
    )
}
