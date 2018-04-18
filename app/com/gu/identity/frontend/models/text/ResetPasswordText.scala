package com.gu.identity.frontend.models.text

import play.api.i18n.Messages


case class ResetPasswordText private(
  pageTitle: String,
  title: String,
  subtitle: String,
  emailAddressField: String,
  button: String,
  socialNetworksTitle: String,
  socialNetworksContent: String,
  userHelpTitle: String,
  userHelpEmail: String,
  userHelpContent: String
)

object ResetPasswordResendText {
  def apply()(implicit messages: Messages): ResetPasswordText =
    ResetPasswordText().copy(
      title = messages("resetPassword.resend.title"),
      subtitle = messages("resetPassword.resend.subtitle"),
      button = messages("resetPassword.resend.button")
    )
}

object ResetPasswordText {
  def apply()(implicit messages: Messages): ResetPasswordText =
    ResetPasswordText(
      pageTitle = messages("resetPassword.pageTitle"),
      title = messages("resetPassword.title"),
      subtitle = messages("resetPassword.subtitle"),
      emailAddressField = messages("resetPassword.emailAddressField"),
      button = messages("resetPassword.button"),
      socialNetworksTitle = messages("resetPassword.socialNetworksTitle"),
      socialNetworksContent = messages("resetPassword.socialNetworksContent"),
      userHelpTitle = messages("resetPassword.userHelpTitle"),
      userHelpEmail = messages("resetPassword.userHelpEmail"),
      userHelpContent = messages("resetPassword.userHelpContent")
    )
}
