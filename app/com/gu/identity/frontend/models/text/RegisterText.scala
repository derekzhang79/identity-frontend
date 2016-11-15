package com.gu.identity.frontend.models.text

import play.api.i18n.Messages

case class RegisterText private(
    `3rdPartyMarketing`: String,
    createAccount: String,
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
    username: String,
    usernameNote: String,
    usernameHelp: String,
    phone: String,
    countryCode: String,

    whyPhone: String,
    becausePhone: String)

object RegisterText {
  def loadText(isMembership : Boolean)(implicit messages: Messages): RegisterText =
    RegisterText(
      `3rdPartyMarketing` = messages("register.3rdPartyMarketing"),
      createAccount = messages("register.createAccount"),
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
      title = if(isMembership) messages("register.title.membership") else messages("register.title"),
      username = messages("register.username"),
      usernameNote = messages("register.usernameNote"),
      usernameHelp = messages("register.usernameHelp"),
      phone = messages("register.phone"),
      countryCode = messages("register.countryCode"),
      whyPhone = messages("register.whyPhone"),
      becausePhone = messages("register.becausePhone")
    )
}

