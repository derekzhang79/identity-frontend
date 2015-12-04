package com.gu.identity.frontend.views.models

import play.api.i18n.Messages
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

case class SignInLinksViewModel(socialFacebook: String = "https://oauth.theguardian.com/facebook/signin",
                                socialGoogle: String = "https://oauth.theguardian.com/google/signin") extends ViewModel {
  def toMap =
    Map("socialFacebook" -> socialFacebook, "socialGoogle" -> socialGoogle)
}

case class SignInViewModel(signInPageText: SignInPageText,
                           layoutText: LayoutText,
                           showPrelude: Boolean = false,
                           links: SignInLinksViewModel = SignInLinksViewModel()) extends ViewModel {
  def toMap =
    Map("signInPageText" -> signInPageText, "layoutText" -> layoutText, "showPrelude" -> showPrelude, "links" -> links.toMap)
}

object SignInViewModel {
  def apply(): SignInViewModel = {
    SignInViewModel(
      SignInPageText(),
      LayoutText()
    )
  }
}

case class SignInPageText (
                      title: String = Messages("signin.title"),
                      pageTitle: String = Messages("signin.pagetitle"),
                      prelude: String = Messages("signin.prelude"),
                      preludeMoreInfo: String = Messages("signin.prelude.moreinfo"),
                      preludeFaq: String = Messages("signin.prelude.faq"),
                      email: String = Messages("signin.email"),
                      divideText: String = Messages("signin.dividetext"),
                      password: String = Messages("signin.password"),
                      forgottenPassword: String = Messages("signin.forgottenpassword"),
                      rememberMe: String = Messages("signin.rememberme"),
                      signIn: String = Messages("signin.signin"),
                      noAccount: String = Messages("signin.noaccount"),
                      signUp: String = Messages("signin.signup"),
                      conditions: String = Messages("signin.conditions"),
                      termsOfService: String = Messages("signin.termsofservice"),
                      privacyPolicy: String = Messages("signin.privacypolicy")
                        )

case class LayoutText (
                      layoutPageTitle: String = Messages("layout.pagetitle")
                        )
