package com.gu.identity.frontend.models.text

import play.api.i18n.Messages

case class TermsText private(
     conditionsText: String,
     termsOfServiceText: String,
     privacyPolicyText: String)

case class TeachersTermsText private(
    basicTermsText: TermsText = TermsText(),
    conditionsText: String)

case class JobsTermsText private(
    basicTermsText: TermsText = TermsText(),
    conditionsText: String)

object TermsText {
  def apply()(implicit messages: Messages): TermsText =
    TermsText(
      conditionsText = messages("terms.conditions"),
      termsOfServiceText = messages("terms.termsOfService"),
      privacyPolicyText = messages("terms.privacyPolicy")
    )
}

object TeachersTermsText {
  def apply()(implicit messages: Messages): TeachersTermsText =
    TeachersTermsText(
      conditionsText = messages("terms.teachersConditions")
    )
}

object JobsTermsText {
  def apply()(implicit messages: Messages): JobsTermsText =
    JobsTermsText(
      conditionsText = messages("terms.jobsConditions")
    )
}
