package com.gu.identity.frontend.models.text

import play.api.i18n.Messages

case class SendSignInLinkText private(
  pageTitle: String,
  title: String
)

object SendSignInLinkText {
  def apply()(implicit messages: Messages): SendSignInLinkText =
    SendSignInLinkText(
      pageTitle = messages("sendSiLink.pageTitle"),
      title = messages("sendSiLink.title")
    )
}
