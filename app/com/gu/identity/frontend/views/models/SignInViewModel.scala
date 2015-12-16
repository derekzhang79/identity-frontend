package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.controllers.routes
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

case class SignInViewModel(showPrelude: Boolean = false,
                           errors: Seq[ErrorViewModel] = Seq.empty,
                           email: String = "",
                           returnUrl: String = "",
                           skipConfirmation: Boolean = false,
                           registerUrl: String = "",
                           forgotPasswordUrl: String = "",
                           links: SignInLinksViewModel = SignInLinksViewModel(),
                           actions: Map[String, String] = Map("signIn" -> routes.SigninAction.signIn().url)) extends ViewModel {
  def toMap(implicit messages: Messages) =
    Map(
      "signInPageText" -> SignInPageText.toMap,
      "layoutText" -> LayoutText.toMap,
      "socialSignInText" -> SocialSignInText.toMap,
      "headerText" -> HeaderText.toMap,
      "footerText" -> FooterText.toMap,
      "showPrelude" -> showPrelude,
      "errors" -> errors.map(_.toMap),
      "email" -> email,
      "returnUrl" -> returnUrl,
      "skipConfirmation" -> skipConfirmation,
      "registerUrl" -> registerUrl,
      "forgotPasswordUrl" -> forgotPasswordUrl,
      "links" -> links.toMap,
      "actions" -> actions)
}

object SignInViewModel {
  def apply(errors: Seq[ErrorViewModel], email: String, returnUrl: Option[String], skipConfirmation: Option[Boolean]): SignInViewModel = {
    val urlParams: Seq[(String, String)] = Seq(returnUrl.map(("returnUrl", _)), skipConfirmation.map(bool => ("skipConfirmation", bool.toString))).flatten

    SignInViewModel(
      errors = errors,
      email = email,
      returnUrl = returnUrl.getOrElse(""),
      skipConfirmation = skipConfirmation.getOrElse(false),
      registerUrl = UrlBuilder("/register", urlParams),
      forgotPasswordUrl = UrlBuilder("/reset", urlParams),
      links = SignInLinksViewModel(urlParams)
    )
  }
}

object UrlBuilder {

  def apply(baseUrl: String, params: Seq[(String, String)]) = {
    val paramString = params.map(x => s"${x._1}=${x._2}").mkString("&")
     paramString match {
       case "" => baseUrl
       case paramString => s"${baseUrl}/?${paramString}"
     }

  }
}
