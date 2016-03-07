package com.gu.identity.frontend.models.text

import play.api.i18n.Messages

case class ResetPasswordEmailSentText private(
                                      pageTitle: String,
                                      title: String,
                                      instructions1: String,
                                      instructions2: String)

object ResetPasswordEmailSentText {
  def apply()(implicit messages: Messages): ResetPasswordEmailSentText =
    ResetPasswordEmailSentText(
      pageTitle = messages("resetPasswordEmailSent.pageTitle"),
      title = messages("resetPasswordEmailSent.title"),
      instructions1 = messages("resetPasswordEmailSent.instructions1"),
      instructions2 = messages("resetPasswordEmailSent.instructions2")
    )
}
