package com.gu.identity.frontend.models.text

import play.api.i18n.Messages

case class TermsText private(
  conditionsText: String,
  privacyText: String
)

case class PartnerTermsText private(
  conditionsText: String,
  privacyText: String
)

object Helpers {
  def makeLink(name: String, url: String) = s"<a href=\042$url\042>$name</a>"
}

object TermsText {
  def apply()(implicit messages: Messages): TermsText = {
    lazy val conditionsLink: String = Helpers.makeLink(
      messages("terms.termsOfService"),
      messages("terms.termsOfServiceUrl")
    )
    lazy val privacyLink: String = Helpers.makeLink(
      messages("terms.privacyPolicy"),
      messages("terms.privacyPolicyUrl")
    )
    TermsText(
      conditionsText = messages("terms.conditions", conditionsLink),
      privacyText = messages("terms.privacy", privacyLink)
    )
  }
}

object PartnerTermsText {
  def apply(
    termsUrl: String,
    privacyUrl: String,
    partnerName: String
  )(implicit messages: Messages): PartnerTermsText = {
    lazy val conditionsLink: String = Helpers.makeLink(
      messages("terms.termsOfService"),
      messages("terms.termsOfServiceUrl")
    )
    lazy val privacyLink: String = Helpers.makeLink(
      messages("terms.privacyPolicy"),
      messages("terms.privacyPolicyUrl")
    )
    lazy val conditionsPartnerLink: String = Helpers.makeLink(
      messages("terms.termsOfService"),
      termsUrl
    )
    lazy val privacyPartnerLink: String = Helpers.makeLink(
      messages("terms.privacyPolicy"),
      privacyUrl
    )
    PartnerTermsText (
      conditionsText = messages("terms.conditionsWithPartner", conditionsLink, messages(partnerName), conditionsPartnerLink),
      privacyText = messages("terms.privacyWithPartner", privacyLink, messages(partnerName), privacyPartnerLink)
    )
  }
}
