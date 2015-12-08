package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.controllers.routes

import com.gu.identity.frontend.models._

case class SignInLinksViewModel(socialFacebook: String = "https://oauth.theguardian.com/facebook/signin",
                                socialGoogle: String = "https://oauth.theguardian.com/google/signin") extends ViewModel {
  def toMap =
    Map("socialFacebook" -> socialFacebook, "socialGoogle" -> socialGoogle)
}

case class ErrorViewModel(id: String, message: String) extends ViewModel {
  def toMap =
    Map("id" -> id, "message" -> message)
}

object ErrorViewModel {

  def apply(id: String): ErrorViewModel = {
    ErrorViewModel(id, getErrorMessage(id))
  }

  val errorMessages = Map(
    "error-gateway" -> "There was a problem signing in, please try again.",
    "error-bad-request" -> "Incorrect email or password, please try again."
  )

  val default: String = "There was an unexpected problem, please try again."

  private def getErrorMessage(id: String) = errorMessages.getOrElse(id, default)
}

case class SignInViewModel(signInPageText: SignInPageText,
                           layoutText: LayoutText,
                           socialSignInText: SocialSignInText,
                           headerText: HeaderText,
                           footerText: FooterText,
                           showPrelude: Boolean = false,
                           errors: Seq[ErrorViewModel] = Seq.empty,
                           email: String = "",
                           returnUrl: String = "",
                           registerUrl: String = "",
                           links: SignInLinksViewModel = SignInLinksViewModel(),
                           actions: Map[String, String] = Map("signIn" -> routes.SigninAction.signIn().url)) extends ViewModel {
  def toMap =
    Map(
      "signInPageText" -> signInPageText,
      "layoutText" -> layoutText,
      "socialSignInText" -> socialSignInText,
      "headerText" -> headerText,
      "footerText" -> footerText,
      "showPrelude" -> showPrelude,
      "errors" -> errors.map(_.toMap),
      "email" -> email,
      "returnUrl" -> returnUrl,
      "registerUrl" -> registerUrl,
      "links" -> links.toMap,
      "actions" -> actions)
}

object SignInViewModel {
  def apply(errors: Seq[ErrorViewModel], email: String, returnUrl: Option[String]): SignInViewModel = {
    val rtnUrl = returnUrl.getOrElse("")
    SignInViewModel(
      SignInPageText(),
      LayoutText(),
      SocialSignInText(),
      HeaderText(),
      FooterText(),
      errors = errors,
      email = email,
      returnUrl = rtnUrl,
      registerUrl = if (rtnUrl == "") "/register" else s"/register?returnUrl=${rtnUrl}"
    )
  }
}
