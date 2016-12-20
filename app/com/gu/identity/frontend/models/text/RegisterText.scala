package com.gu.identity.frontend.models.text

import com.gu.identity.frontend.models.{ClientID, GuardianMembersClientID}
import play.api.i18n.Messages

case class RegisterText private(
                                 `3rdPartyMarketing`: String,
                                 createAccount: String,
                                 continue: String,
                                 divideText: String,
                                 email: String,
                                 emailHelp: String,
                                 firstName: String,
                                 gnmMarketing: String,
                                 lastName: String,
                                 firstOrLastNameHelp: String,
                                 name: String,
                                 pageTitle: String,
                                 password: String,
                                 passwordHelp: String,
                                 signIn: String,
                                 signInCta: String,
                                 standfirst: String,
                                 title: String,
                                 displayName: String,
                                 displayNameNote: String,
                                 displayNameHelp: String,
                                 displayNameHelpShortened: String,
                                 displayNameHelpExpanded: String,
                                 phone: String,
                                 countryCode: String,

                                 whyPhone: String,
                                 becausePhone: String)

object RegisterText {
  def loadText(clientId : Option[ClientID])(implicit messages: Messages): RegisterText =
    RegisterText(
      `3rdPartyMarketing` = messages("register.3rdPartyMarketing"),
      createAccount = messages("register.createAccount"),
      continue = messages("register.continue"),
      divideText = messages("register.divideText"),
      email = messages("register.email"),
      emailHelp = messages("register.emailHelp"),
      firstName = messages("register.firstName"),
      gnmMarketing = messages("register.gnmMarketing"),
      lastName = messages("register.lastName"),
      firstOrLastNameHelp = messages("register.firstOrLastNameHelp"),
      name = messages("register.name"),
      pageTitle = messages("register.pageTitle"),
      password = messages("register.password"),
      passwordHelp = messages("register.passwordHelp"),
      signIn = messages("register.signIn"),
      signInCta = messages("register.signInCta"),
      standfirst = messages("register.standfirst"),
      title = clientId match {
        case Some(GuardianMembersClientID) => messages("register.title.supporter")
        case _ => messages("register.title")
      },
      displayName = messages("register.displayName"),
      displayNameNote = messages("register.displayNameNote"),
      displayNameHelp = messages("register.displayNameHelp"),
      displayNameHelpShortened = messages("register.displayNameHelpShortened"),
      displayNameHelpExpanded = messages("register.displayNameHelpExpanded"),
      phone = messages("register.phone"),
      countryCode = messages("register.countryCode"),
      whyPhone = messages("register.whyPhone"),
      becausePhone = messages("register.becausePhone")
    )
}

