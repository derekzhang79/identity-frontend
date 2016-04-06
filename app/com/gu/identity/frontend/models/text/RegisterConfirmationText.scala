package com.gu.identity.frontend.models.text

import play.api.i18n.Messages

case class RegisterConfirmationText private(
    pageTitle: String,
    userName: String,
    title: String,
    titleStandfirst: String,
    oneMoreStepTitle: String,
    oneMoreStepP1: String,
    oneMoreStepP2: String,
    weValueYourOpinionTitle: String,
    weValueYourOpinionP1: String,
    weValueYourOpinionP2: String,
    weValueYourOpinionP3: String,
    completeRegistration: String)

object RegisterConfirmationText {
  def apply()(implicit messages: Messages): RegisterConfirmationText =
    RegisterConfirmationText(
      pageTitle = messages("registerConfirmation.pageTitle"),
      userName = messages("registerConfirmation.userName"),
      title = messages("registerConfirmation.title"),
      titleStandfirst = messages("registerConfirmation.titleStandfirst"),
      oneMoreStepTitle = messages("registerConfirmation.oneMoreStepTitle"),
      oneMoreStepP1 = messages("registerConfirmation.oneMoreStepP1"),
      oneMoreStepP2 = messages("registerConfirmation.oneMoreStepP2"),
      weValueYourOpinionTitle = messages("registerConfirmation.weValueYourOpinionTitle"),
      weValueYourOpinionP1 = messages("registerConfirmation.weValueYourOpinionP1"),
      weValueYourOpinionP2 = messages("registerConfirmation.weValueYourOpinionP2"),
      weValueYourOpinionP3 = messages("registerConfirmation.weValueYourOpinionP3"),
      completeRegistration = messages("registerConfirmation.completeRegistration")
    )
}




