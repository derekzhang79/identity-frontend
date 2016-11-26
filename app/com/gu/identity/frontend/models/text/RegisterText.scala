package com.gu.identity.frontend.models.text

import com.gu.identity.frontend.models.{ClientID, GuardianMembersAClientID, GuardianMembersBClientID}
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
                                 name: String,
                                 pageTitle: String,
                                 password: String,
                                 passwordHelp: String,
                                 signIn: String,
                                 signInCta: String,
                                 standfirst: String,
                                 title: String,
                                 displayname: String,
                                 displaynameNote: String,
                                 displaynameHelp: String,
                                 displaynameHelpShortened: String,
                                 displaynameHelpExpanded: String,
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
      name = messages("register.name"),
      pageTitle = messages("register.pageTitle"),
      password = messages("register.password"),
      passwordHelp = messages("register.passwordHelp"),
      signIn = messages("register.signIn"),
      signInCta = messages("register.signInCta"),
      standfirst = messages("register.standfirst"),
      title = clientId match {
        case Some(GuardianMembersAClientID) => messages("register.title.membership")
        case Some(GuardianMembersBClientID) => messages("register.title.supporter")
        case _ => messages("register.title")
      },
      displayname = messages("register.username"),
      displaynameNote = messages("register.usernameNote"),
      displaynameHelp = messages("register.usernameHelp"),
      displaynameHelpShortened = messages("register.usernameHelpShortened"),
      displaynameHelpExpanded = messages("register.usernameHelpExpanded"),
      phone = messages("register.phone"),
      countryCode = messages("register.countryCode"),
      whyPhone = messages("register.whyPhone"),
      becausePhone = messages("register.becausePhone")
    )
}

