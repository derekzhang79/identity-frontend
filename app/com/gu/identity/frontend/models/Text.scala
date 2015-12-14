package com.gu.identity.frontend.models

import play.api.i18n.MessagesApi

object Text {
  object SignInPageText {
    def toMap(implicit messages: MessagesApi): Map[String, String] = {
      Map (
        "title" -> messages("signin.title"),
        "pageTitle" -> messages("signin.pagetitle"),
        "prelude" -> messages("signin.prelude"),
        "preludeMoreInfo" -> messages("signin.prelude.moreinfo"),
        "preludeFaq" -> messages("signin.prelude.faq"),
        "email" -> messages("signin.email"),
        "divideText" -> messages("signin.dividetext"),
        "password" -> messages("signin.password"),
        "forgottenPassword" -> messages("signin.forgottenpassword"),
        "rememberMe" -> messages("signin.rememberme"),
        "signIn" -> messages("signin.signin"),
        "noAccount" -> messages("signin.noaccount"),
        "signUp" -> messages("signin.signup"),
        "conditions" -> messages("signin.conditions"),
        "termsOfService" -> messages("signin.termsofservice"),
        "privacyPolicy" -> messages("signin.privacypolicy")
      )
    }
  }

  object LayoutText {
    def toMap(implicit messages: MessagesApi): Map[String, String] = {
      Map(
        "layoutPageTitle" -> messages("layout.pagetitle")
      )
    }
  }

  object SocialSignInText {
    def toMap(implicit messages: MessagesApi): Map[String, String] = {
      Map(
        "facebook" -> messages("socialsignin.description", "Facebook"),
        "google" -> messages("socialsignin.description", "Google")
      )
    }
  }

  object HeaderText {
    def toMap(implicit messages: MessagesApi): Map[String, String] = {
      Map(
        "back" -> messages("header.backtext"),
        "logo" -> messages("header.logo")
      )
    }
  }

  object FooterText {
    def toMap(implicit messages: MessagesApi): Map[String, String] = {
      Map(
        "help" -> messages("footer.help"),
        "terms" -> messages("footer.terms"),
        "contact" -> messages("footer.contact"),
        "privacy" -> messages("footer.privacy"),
        "techFeedback" -> messages("footer.techfeedback"),
        "cookies" -> messages("footer.cookies"),
        "copyright" -> messages("footer.copyright")
      )
    }
  }
}
