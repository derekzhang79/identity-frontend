package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.models.{UrlBuilder, ReturnUrl}
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
      configuration: Configuration,
      text: OAuthText,
      returnUrl: ReturnUrl,
      skipConfirmation: Option[Boolean])
      (implicit messages: Messages): OAuthProviderViewModel = {

    val baseUrl = configuration.identityFederationApiHost + provider.authPath

    provider match {
      case GoogleOAuth =>
        OAuthProviderViewModel(provider.id, text.google, UrlBuilder(baseUrl, returnUrl, skipConfirmation))

      case FacebookOAuth =>
        OAuthProviderViewModel(provider.id, text.facebook, UrlBuilder(baseUrl, returnUrl, skipConfirmation))
    }
  }

}

sealed trait SupportedOAuthProvider {
  val id: String
  val authPath: String
}

object SupportedOAuthProvider {
  val all: Seq[SupportedOAuthProvider] = Seq(FacebookOAuth, GoogleOAuth)
}

case object FacebookOAuth extends SupportedOAuthProvider {
  val id = "facebook"
  val authPath = "/facebook/signin"
}

case object GoogleOAuth extends SupportedOAuthProvider {
  val id = "google"
  val authPath = "/google/signin"
}


case class OAuthSignInViewModel private(
    all: Seq[OAuthProviderViewModel])
  extends OAuthViewModel

object OAuthSignInViewModel {

  def apply(configuration: Configuration, returnUrl: ReturnUrl, skipConfirmation: Option[Boolean])(implicit messages: Messages): OAuthSignInViewModel = {
    val text = OAuthSignInText()

    OAuthSignInViewModel(
      SupportedOAuthProvider.all.map(OAuthProviderViewModel(_, configuration, text, returnUrl, skipConfirmation))
    )
  }

}


case class OAuthRegistrationViewModel(
    all: Seq[OAuthProviderViewModel])
  extends OAuthViewModel

object OAuthRegistrationViewModel {

  def apply(configuration: Configuration, returnUrl: ReturnUrl, skipConfirmation: Option[Boolean])(implicit messages: Messages): OAuthRegistrationViewModel = {
    val text = OAuthRegistrationText()

    OAuthRegistrationViewModel(
      SupportedOAuthProvider.all.map(OAuthProviderViewModel(_, configuration, text, returnUrl, skipConfirmation))
    )
  }

}
