package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.models._

case class SignInLinksViewModel(socialFacebook: String = "https://oauth.theguardian.com/facebook/signin",
                                socialGoogle: String = "https://oauth.theguardian.com/google/signin") extends ViewModel {
  def toMap =
    Map("socialFacebook" -> socialFacebook, "socialGoogle" -> socialGoogle)
}

case class SignInViewModel(signInPageText: SignInPageText,
                           layoutText: LayoutText,
                           socialSignInText: SocialSignInText,
                           showPrelude: Boolean = false,
                           links: SignInLinksViewModel = SignInLinksViewModel()) extends ViewModel {
  def toMap =
    Map("signInPageText" -> signInPageText, "layoutText" -> layoutText, "socialSignInText" -> socialSignInText, "showPrelude" -> showPrelude, "links" -> links.toMap)
}

object SignInViewModel {
  def apply(): SignInViewModel = {
    SignInViewModel(
      SignInPageText(),
      LayoutText(),
      SocialSignInText()
    )
  }
}
