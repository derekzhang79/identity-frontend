package com.gu.identity.frontend.views.models

import play.api.i18n.Messages
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

case class SignInLinksViewModel(socialFacebook: String = "https://oauth.theguardian.com/facebook/signin",
                                socialGoogle: String = "https://oauth.theguardian.com/google/signin") extends ViewModel {
  def toMap =
    Map("socialFacebook" -> socialFacebook, "socialGoogle" -> socialGoogle)
}

case class SignInViewModel(pageTitle: String,
                           pageText: SignInPageText,
                           showPrelude: Boolean = false,
                           links: SignInLinksViewModel = SignInLinksViewModel()) extends ViewModel {
  def toMap =
    Map("pageTitle" -> pageTitle, "pageText" -> pageText, "showPrelude" -> showPrelude, "links" -> links.toMap)
}

object SignInViewModel {
  def apply(): SignInViewModel = {
    SignInViewModel(
      Messages("signin.pagetitle"),
      SignInPageText()
    )
  }
}

case class SignInPageText (
                      title: String = Messages("signin.title"),
                      prelude: String = Messages("signin.prelude"),
                      preludeMoreInfo: String = Messages("signin.prelude.moreinfo"),
                      preludeFaq: String = Messages("signin.prelude.faq")
                        )
