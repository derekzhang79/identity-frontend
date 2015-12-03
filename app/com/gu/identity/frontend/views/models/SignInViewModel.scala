package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.controllers.routes

case class SignInLinksViewModel(socialFacebook: String = "https://oauth.theguardian.com/facebook/signin",
                                socialGoogle: String = "https://oauth.theguardian.com/google/signin") extends ViewModel {
  def toMap =
    Map("socialFacebook" -> socialFacebook, "socialGoogle" -> socialGoogle)
}

case class SignInViewModel(title: String = "Sign in to the Guardian",
                           pageTitle: String = "Sign in",
                           showPrelude: Boolean = false,
                           links: SignInLinksViewModel = SignInLinksViewModel(),
                           actions: Map[String, String] = Map("signIn" -> routes.SigninAction.signIn().url)) extends ViewModel {
  def toMap =
    Map("title" -> title, "pageTitle" -> pageTitle, "showPrelude" -> showPrelude, "links" -> links.toMap, "actions" -> actions)
}
