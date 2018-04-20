package com.gu.identity.frontend.models.text

import play.api.i18n.Messages

case class TermsText private(
     conditionsText: String,
     dataText: String,
     termsOfServiceText: String,
     termsOfServiceUrl: String,
     privacyPolicyText: String,
     privacyPolicyUrl: String)

case class TeachersTermsText private(
    basicTermsText: TermsText,
    conditionsText: String)

case class JobsTermsText private(
    basicTermsText: TermsText,
    conditionsText: String)

object TermsText {
  def apply()(implicit messages: Messages): TermsText =
    TermsText(
      conditionsText = messages("terms.conditions"),
      dataText = messages("terms.dataConditions"),
      termsOfServiceText = messages("terms.termsOfService"),
      termsOfServiceUrl = messages("terms.termsOfServiceUrl"),
      privacyPolicyText = messages("terms.privacyPolicy"),
      privacyPolicyUrl = messages("terms.privacyPolicyUrl")
    )
}

object TeachersTermsText {
  def apply()(implicit messages: Messages): TeachersTermsText =
    TeachersTermsText(
      basicTermsText = TermsText(),
      conditionsText = messages("terms.teachersConditions")
    )
}

object JobsTermsText {
  def apply()(implicit messages: Messages): JobsTermsText =
    JobsTermsText(
      basicTermsText = TermsText(),
      conditionsText = messages("terms.jobsConditions")
    )
}
