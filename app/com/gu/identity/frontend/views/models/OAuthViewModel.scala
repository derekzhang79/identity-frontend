package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.models.{GuardianMembersClientID, ClientID, UrlBuilder, ReturnUrl}
import com.gu.identity.frontend.models.text.{OAuthText, OAuthRegistrationText, OAuthSignInText, OAuthPermissionsText}
import com.gu.identity.frontend.mvt._
import play.api.i18n.Messages


sealed trait OAuthViewModel extends ViewModel {
  val all: Seq[OAuthProviderViewModel]
  val permission: Option[String]
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
      skipConfirmation: Option[Boolean],
      clientId: Option[ClientID])
      (implicit messages: Messages): OAuthProviderViewModel = {

    val baseUrl = configuration.identityFederationApiHost + provider.authPath

    provider match {
      case GoogleOAuth =>
        OAuthProviderViewModel(provider.id, text.google, UrlBuilder(baseUrl, returnUrl, skipConfirmation, clientId))

      case FacebookOAuth =>
        OAuthProviderViewModel(provider.id, text.facebook, UrlBuilder(baseUrl, returnUrl, skipConfirmation, clientId))
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
    all: Seq[OAuthProviderViewModel], permission: Option[String])
  extends OAuthViewModel

object OAuthSignInViewModel {

  def apply(configuration: Configuration, returnUrl: ReturnUrl, skipConfirmation: Option[Boolean], clientId: Option[ClientID], activeTests: ActiveMultiVariantTests)(implicit messages: Messages): OAuthSignInViewModel = {
    val text = OAuthSignInText()
    val permission = PermissionCopy(activeTests, OAuthPermissionsText())

    OAuthSignInViewModel(
      SupportedOAuthProvider.all.map(OAuthProviderViewModel(_, configuration, text, returnUrl, skipConfirmation, clientId)), permission
    )
  }
}

case class OAuthRegistrationViewModel(
    all: Seq[OAuthProviderViewModel], permission: Option[String])
  extends OAuthViewModel

object OAuthRegistrationViewModel {

  def apply(configuration: Configuration, returnUrl: ReturnUrl, skipConfirmation: Option[Boolean], clientId: Option[ClientID], activeTests: ActiveMultiVariantTests)(implicit messages: Messages): OAuthRegistrationViewModel = {
    val text = OAuthRegistrationText()
    val permission = PermissionCopy(activeTests, OAuthPermissionsText())

    OAuthRegistrationViewModel(
      SupportedOAuthProvider.all.map(OAuthProviderViewModel(_, configuration, text, returnUrl, skipConfirmation, clientId)), permission
    )
  }
}

object PermissionCopy {

  def apply(activeTests: ActiveMultiVariantTests, text: OAuthPermissionsText): Option[String] = {
    activeTests.get(SocialSigninPermissionCopyTest) match {
      case Some(SocialSigninPermissionCopyVariantA) => Some(text.a)
      case Some(SocialSigninPermissionCopyVariantB) => Some(text.b)
      case _ => None
      }
    }
  }

