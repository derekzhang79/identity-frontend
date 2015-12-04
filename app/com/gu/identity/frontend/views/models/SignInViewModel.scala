package com.gu.identity.frontend.views.models

import play.api.i18n.Messages
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

case class SignInLinksViewModel(socialFacebook: String = "https://oauth.theguardian.com/facebook/signin",
                                socialGoogle: String = "https://oauth.theguardian.com/google/signin") extends ViewModel {
  def toMap =
    Map("socialFacebook" -> socialFacebook, "socialGoogle" -> socialGoogle)
}

case class SignInViewModel(title: String = "Sign in to the Guardian",
                           pageTitle: String = "Sign in",
                           showPrelude: Boolean = false,
                           links: SignInLinksViewModel = SignInLinksViewModel()) extends ViewModel {
  def toMap =
    Map("title" -> title, "pageTitle" -> pageTitle, "showPrelude" -> showPrelude, "links" -> links.toMap)
}

object SignInViewModel {
  def apply(): SignInViewModel = {
    SignInViewModel(
      Messages("signin.title"),
      Messages("signin.pagetitle")
    )
  }
}
