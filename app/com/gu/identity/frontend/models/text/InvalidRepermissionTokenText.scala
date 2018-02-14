package com.gu.identity.frontend.models.text

import play.api.i18n.Messages

case class InvalidRepermissionTokenText private(
                                            pageTitle: String,
                                            title: String,
                                            description: String,
                                            buttonText: String
                                          )

object InvalidRepermissionTokenText {
  def apply()(implicit messages: Messages): InvalidRepermissionTokenText =
    InvalidRepermissionTokenText(
      pageTitle = messages("resendRepermissionLink.pageTitle"),
      title = messages("resendRepermissionLink.title"),
      description = messages("resendRepermissionLink.description"),
      buttonText = messages("resendRepermissionLink.buttonText")
    )
}
