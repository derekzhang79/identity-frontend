package com.gu.identity.frontend.models.text

import play.api.i18n.Messages

case class SendSignInLinkText private(
  pageTitle: String,
  title: String,
  emailInput: String,
  submitAction: String
)

object SendSignInLinkText {
  def apply()(implicit messages: Messages): SendSignInLinkText =
    SendSignInLinkText(
      pageTitle = messages("sendSiLink.pageTitle"),
      title = messages("sendSiLink.title"),
      emailInput = messages("sendSiLink.emailInput"),
      submitAction = messages("sendSiLink.submitAction")
    )
}
