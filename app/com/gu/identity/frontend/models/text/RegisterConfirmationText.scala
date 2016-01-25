package com.gu.identity.frontend.models.text

import play.api.i18n.Messages

case class RegisterConfirmationText private(
    pageTitle: String,
    title: String,
    instruction: String,
    completeRegistration: String,
    faq: String,
    emailUserhelp: String,
    resetPassword: String,
    signOut: String)

object RegisterConfirmationText {
  def apply()(implicit messages: Messages): RegisterConfirmationText =
    RegisterConfirmationText(
      pageTitle = messages("registerConfirmation.pageTitle"),
      title = messages("registerConfirmation.title"),
      instruction = messages("registerConfirmation.instruction"),
      completeRegistration = messages("registerConfirmation.completeRegistration"),
      faq = messages("registerConfirmation.faq"),
      emailUserhelp = messages("registerConfirmation.emailUserhelp"),
      resetPassword = messages("registerConfirmation.resetPassword"),
      signOut = messages("registerConfirmation.signOut")
    )
}
