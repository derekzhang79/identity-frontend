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
      pageTitle = messages("sendSignInLink.pageTitle"),
      title = messages("sendSignInLink.title"),
      emailInput = messages("sendSignInLink.emailInput"),
      submitAction = messages("sendSignInLink.submitAction")
    )
}
