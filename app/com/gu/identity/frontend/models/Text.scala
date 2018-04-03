package com.gu.identity.frontend.models

import play.api.i18n.Messages

object Text {
  object SignInPageText {
    def toMap(isMembership: Boolean)(implicit messages: Messages): Map[String, String] = {
      Map (
        "title" -> (if(isMembership) messages("signin.title.supporter") else messages("signin.title")),
        "pageTitle" -> messages("signin.pagetitle"),
        "prelude" -> messages("signin.prelude"),
        "preludeMoreInfo" -> messages("signin.prelude.moreinfo"),
        "preludeFaq" -> messages("signin.prelude.faq"),
        "email" -> messages("signin.email"),
        "signInWithEmail" -> messages("signin.signInWithEmail"),
        "divideText" -> messages("signin.dividetext"),
        "password" -> messages("signin.password"),
        "forgottenPassword" -> messages("signin.forgottenpassword"),
        "rememberMe" -> messages("signin.rememberme"),
        "signIn" -> messages("signin.signin"),
        "noAccount" -> messages("signin.noaccount"),
        "signUp" -> messages("signin.signup"),
        "conditions" -> messages("signin.conditions"),
        "continue" -> messages("signin.continue"),
        "termsOfService" -> messages("signin.termsofservice"),
        "privacyPolicy" -> messages("signin.privacypolicy")
      )
    }
  }

  object TwoStepSignInPageText {
    def toMap(isMembership: Boolean)(implicit messages: Messages): Map[String, String] = {
      Map (
        "title" -> (if(isMembership) messages("signin.title.supporter") else messages("signin.title")),
        "pageTitle" -> messages("signin.pagetitle"),
        "prelude" -> messages("signin.prelude"),
        "preludeMoreInfo" -> messages("signin.prelude.moreinfo"),
        "preludeFaq" -> messages("signin.prelude.faq"),
        "email" -> messages("signin.email"),
        "signInWithEmail" -> messages("signin.signInWithEmail"),
        "password" -> messages("signin.password"),
        "forgottenPassword" -> messages("signin.forgottenpassword"),
        "rememberMe" -> messages("signin.rememberme"),
        "signIn" -> messages("signin.signin"),
        "noAccount" -> messages("signin.noaccount"),
        "signUp" -> messages("signin.signup"),
        "conditions" -> messages("signin.conditions"),
        "continue" -> messages("signin.continue"),
        "termsOfService" -> messages("signin.termsofservice"),
        "privacyPolicy" -> messages("signin.privacypolicy"),

        "emailFieldTitle" -> messages("signinTwoStep.emailFieldTitle"),
        "setPasswordTitle" -> messages("signinTwoStep.setPasswordTitle"),
        "setPasswordAction" -> messages("signinTwoStep.setPasswordAction"),
        "divideText" -> messages("signinTwoStep.dividetext"),
        "welcome" -> messages("signinTwoStep.welcome"),
        "changeEmailLink" -> messages("signinTwoStep.changeEmailLink"),
        "signInAction" -> messages("signinTwoStep.signInAction"),
        "continueAction" -> messages("signinTwoStep.continueAction"),
        "unknownEmailTitle" -> messages("signinTwoStep.unknownEmailTitle"),
        "registerAction" -> messages("signinTwoStep.registerAction"),
        "passwordFieldTitle" -> messages("signinTwoStep.passwordFieldTitle")
      )
    }
  }

  object LayoutText {
    def toMap(implicit messages: Messages): Map[String, String] = {
      Map(
        "layoutPageTitle" -> messages("layout.pagetitle")
      )
    }
  }

  object ClientSideText {
    def toMap(implicit messages: Messages): Map[String, String] = {
      Map()
    }
  }

  object HeaderText {
    def toMap(implicit messages: Messages): Map[String, String] = {
      Map(
        "back" -> messages("header.backtext"),
        "logo" -> messages("header.logo")
      )
    }
  }
}
