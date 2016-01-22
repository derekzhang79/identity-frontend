package com.gu.identity.frontend.models.text

import play.api.i18n.Messages

case class TermsText private(
     conditionsText: String,
     termsOfServiceText: String,
     privacyPolicyText: String)

object TermsText {
  def apply()(implicit messages: Messages): TermsText =
    TermsText(
      conditionsText = messages("terms.conditions"),
      termsOfServiceText = messages("terms.termsOfService"),
      privacyPolicyText = messages("terms.privacyPolicy")
    )
}
