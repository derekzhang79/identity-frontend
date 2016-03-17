package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.models.text.{OAuthPermissionsText, OAuthRegistrationText, OAuthSignInText, OAuthText}
import com.gu.identity.frontend.models.{ClientID, GroupCode, ReturnUrl, UrlBuilder}
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
      finalReturnUrl: ReturnUrl,
      skipConfirmation: Option[Boolean],
      clientId: Option[ClientID],
      groupCode: Option[GroupCode])
      (implicit messages: Messages): OAuthProviderViewModel = {

    val baseUrl = configuration.identityFederationApiHost + provider.authPath

    val returnUrl = groupCode match{
      case Some(code) => UrlBuilder.buildOauthReturnUrl(baseUrl, finalReturnUrl, skipConfirmation, clientId, code, configuration)
      case None => UrlBuilder(baseUrl, finalReturnUrl, skipConfirmation, clientId)
    }



    provider match {
      case GoogleOAuth =>
        OAuthProviderViewModel(provider.id, text.google, returnUrl)

      case FacebookOAuth =>
        OAuthProviderViewModel(provider.id, text.facebook, returnUrl)
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

  def apply(configuration: Configuration, returnUrl: ReturnUrl, skipConfirmation: Option[Boolean], clientId: Option[ClientID], groupCode: Option[GroupCode], activeTests: ActiveMultiVariantTests)(implicit messages: Messages): OAuthSignInViewModel = {

    val text = OAuthSignInText()
    val permission = PermissionCopy(activeTests, OAuthPermissionsText())

    OAuthSignInViewModel(
      SupportedOAuthProvider.all.map(OAuthProviderViewModel(_, configuration, text, returnUrl, skipConfirmation, clientId, groupCode)), permission
    )
  }
}

case class OAuthRegistrationViewModel(
    all: Seq[OAuthProviderViewModel], permission: Option[String])
  extends OAuthViewModel

object OAuthRegistrationViewModel {

  def apply(configuration: Configuration, returnUrl: ReturnUrl, skipConfirmation: Option[Boolean], clientId: Option[ClientID], groupCode: Option[GroupCode], activeTests: ActiveMultiVariantTests)(implicit messages: Messages): OAuthRegistrationViewModel = {
    val text = OAuthRegistrationText()
    val permission = PermissionCopy(activeTests, OAuthPermissionsText())

    OAuthRegistrationViewModel(
      SupportedOAuthProvider.all.map(OAuthProviderViewModel(_, configuration, text, returnUrl, skipConfirmation, clientId, groupCode)), permission
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

