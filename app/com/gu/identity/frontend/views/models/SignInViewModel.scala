package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.controllers.routes
import com.gu.identity.frontend.models.ReturnUrl
import com.gu.identity.frontend.models.Text._
import play.api.i18n.Messages

case class SignInLinksViewModel(socialFacebook: String = "https://oauth.theguardian.com/facebook/signin",
                                socialGoogle: String = "https://oauth.theguardian.com/google/signin") extends ViewModel {
  def toMap(implicit messages: Messages) =
    Map("socialFacebook" -> socialFacebook, "socialGoogle" -> socialGoogle)
}

object SignInLinksViewModel {
  def apply(urlParams: Seq[(String, String)]): SignInLinksViewModel = {
      SignInLinksViewModel(
        socialFacebook = UrlBuilder("https://oauth.theguardian.com/facebook/signin", urlParams),
        socialGoogle = UrlBuilder("https://oauth.theguardian.com/google/signin", urlParams)
    )
  }
}

case class SignInViewModel(
    layout: LayoutViewModel,
    signInPageText: Map[String, String],
    socialSignInText: Map[String, String],
    showPrelude: Boolean = false,
    errors: Seq[ErrorViewModel] = Seq.empty,
    returnUrl: String = "",
    skipConfirmation: Boolean = false,
    registerUrl: String = "",
    forgotPasswordUrl: String = "",
    links: SignInLinksViewModel = SignInLinksViewModel(),
    actions: Map[String, String] = Map("signIn" -> routes.SigninAction.signIn().url),
    resources: Seq[PageResource with Product],
    indirectResources: Seq[PageResource with Product])
  extends ViewModel
  with ViewModelResources


object SignInViewModel {
  def apply(configuration: Configuration, errors: Seq[ErrorViewModel], returnUrl: ReturnUrl, skipConfirmation: Option[Boolean])(implicit messages: Messages): SignInViewModel = {
    val urlParams: Seq[(String, String)] = Seq(Some("returnUrl" -> returnUrl.url), skipConfirmation.map(bool => ("skipConfirmation", bool.toString))).flatten

    val layout = LayoutViewModel(configuration)

    SignInViewModel(
      layout = layout,
      signInPageText = SignInPageText.toMap,
      socialSignInText = SocialSignInText.toMap,
      errors = errors,
      returnUrl = returnUrl.url,
      skipConfirmation = skipConfirmation.getOrElse(false),
      registerUrl = UrlBuilder("/register", urlParams),
      forgotPasswordUrl = UrlBuilder("/reset", urlParams),
      links = SignInLinksViewModel(urlParams),
      resources = layout.resources,
      indirectResources = layout.indirectResources
    )
  }
}

object UrlBuilder {

  def apply(baseUrl: String, params: Seq[(String, String)]) = {
    val paramString = params.map(x => s"${x._1}=${x._2}").mkString("&")
     paramString match {
       case "" => baseUrl
       case paramString => s"$baseUrl?$paramString"
     }

  }
}
