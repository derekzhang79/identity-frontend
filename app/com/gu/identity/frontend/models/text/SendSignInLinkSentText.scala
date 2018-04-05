package com.gu.identity.frontend.models.text

import play.api.i18n.Messages

case class SendSignInLinkSentText private(
  pageTitle: String,
  title: String
)

object SendSignInLinkSentText  {
  def apply()(implicit messages: Messages): SendSignInLinkSentText =
    SendSignInLinkSentText(
      pageTitle = messages("sendSiLinkSent.pageTitle"),
      title = messages("sendSiLinkSent.title")
    )
}
