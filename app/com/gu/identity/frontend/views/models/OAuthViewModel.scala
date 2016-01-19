package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.models.ReturnUrl
import com.gu.identity.frontend.models.text.{OAuthText, OAuthRegistrationText, OAuthSignInText}
import play.api.i18n.Messages


sealed trait OAuthViewModel extends ViewModel {
  val all: Seq[OAuthProviderViewModel]
}

case class OAuthProviderViewModel(
  id: String,
  text: String,
  url: String) extends ViewModel

object OAuthProviderViewModel {
  def apply(
      provider: SupportedOAuthProvider,
      text: OAuthText,
      returnUrl: ReturnUrl,
      skipConfirmation: Option[Boolean])
      (implicit messages: Messages): OAuthProviderViewModel = {

    val params = getOAuthEndpointParams(returnUrl, skipConfirmation)

    provider match {
      case p @ GoogleOAuth =>
        OAuthProviderViewModel(p.id, text.google, UrlBuilder(p.url, params))

      case p @ FacebookOAuth =>
        OAuthProviderViewModel(p.id, text.facebook, UrlBuilder(p.url, params))
    }
  }

  private def getOAuthEndpointParams(
      returnUrl: ReturnUrl,
      skipConfirmation: Option[Boolean]): Seq[(String, String)] = Seq(
    Some("returnUrl" -> returnUrl.url),
    skipConfirmation.map("skipConfirmation" -> _.toString)
  ).flatten

}

sealed trait SupportedOAuthProvider {
  val id: String
  val url: String
}

object SupportedOAuthProvider {
  val all: Seq[SupportedOAuthProvider] = Seq(FacebookOAuth, GoogleOAuth)
}

case object FacebookOAuth extends SupportedOAuthProvider {
  val id = "facebook"
  val url = "https://oauth.theguardian.com/facebook/signin"
}

case object GoogleOAuth extends SupportedOAuthProvider {
  val id = "google"
  val url = "https://oauth.theguardian.com/google/signin"
}


case class OAuthSignInViewModel private(
    all: Seq[OAuthProviderViewModel])
  extends OAuthViewModel

object OAuthSignInViewModel {

  def apply(returnUrl: ReturnUrl, skipConfirmation: Option[Boolean])(implicit messages: Messages): OAuthSignInViewModel = {
    val text = OAuthSignInText()

    OAuthSignInViewModel(
      SupportedOAuthProvider.all.map(OAuthProviderViewModel(_, text, returnUrl, skipConfirmation))
    )
  }

}


case class OAuthRegistrationViewModel(
    all: Seq[OAuthProviderViewModel])
  extends OAuthViewModel

object OAuthRegistrationViewModel {

  def apply(returnUrl: ReturnUrl, skipConfirmation: Option[Boolean])(implicit messages: Messages): OAuthRegistrationViewModel = {
    val text = OAuthRegistrationText()

    OAuthRegistrationViewModel(
      SupportedOAuthProvider.all.map(OAuthProviderViewModel(_, text, returnUrl, skipConfirmation))
    )
  }

}
