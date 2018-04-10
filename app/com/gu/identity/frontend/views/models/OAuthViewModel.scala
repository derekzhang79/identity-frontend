package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.models.text.{OAuthRegistrationText, OAuthSignInText, OAuthText}
import com.gu.identity.frontend.models.{ClientID, GroupCode, ReturnUrl, UrlBuilder}
import com.gu.identity.frontend.mvt._
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
      finalReturnUrl: ReturnUrl,
      skipConfirmation: Option[Boolean],
      clientId: Option[ClientID],
      skipConsentJourney: Option[Boolean],
      groupCode: Option[GroupCode])
      (implicit messages: Messages): OAuthProviderViewModel = {

    val baseUrl = configuration.identityFederationApiHost + provider.authPath

    val returnUrl = groupCode match{
      case Some(code) => UrlBuilder.buildOauthReturnUrl(baseUrl, finalReturnUrl, skipConfirmation, clientId, code, configuration, skipConsentJourney)
      case None => UrlBuilder(baseUrl, finalReturnUrl, skipConfirmation, clientId, group = None)
    }

    println(returnUrl)

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
    all: Seq[OAuthProviderViewModel])
  extends OAuthViewModel

object OAuthSignInViewModel {

  def apply(configuration: Configuration, returnUrl: ReturnUrl, skipConfirmation: Option[Boolean], clientId: Option[ClientID], groupCode: Option[GroupCode], activeTests: ActiveMultiVariantTests, skipConsentJourney:Option[Boolean])(implicit
                                                                                                                                                                                                                               messages: Messages)
  : OAuthSignInViewModel = {

    val text = OAuthSignInText()

    OAuthSignInViewModel(
      SupportedOAuthProvider.all.map(OAuthProviderViewModel(_, configuration, text, returnUrl, skipConfirmation, clientId, skipConsentJourney, groupCode))
    )
  }
}

case class OAuthRegistrationViewModel(
    all: Seq[OAuthProviderViewModel])
  extends OAuthViewModel

object OAuthRegistrationViewModel {

  def apply(configuration: Configuration, returnUrl: ReturnUrl, skipConfirmation: Option[Boolean], clientId: Option[ClientID], groupCode: Option[GroupCode], activeTests: ActiveMultiVariantTests, skipConsentJourney: Option[Boolean])(implicit
                                                                                                                                                                                                                               messages: Messages): OAuthRegistrationViewModel = {
    val text = OAuthRegistrationText()

    OAuthRegistrationViewModel(
      SupportedOAuthProvider.all.map(OAuthProviderViewModel(_, configuration, text, returnUrl, skipConfirmation, clientId, skipConsentJourney, groupCode))
    )
  }
}


