package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.controllers.routes
import com.gu.identity.frontend.models.Text._

import com.gu.identity.frontend.models._
import play.api.i18n.Messages

case class SignInLinksViewModel(socialFacebook: String = "https://oauth.theguardian.com/facebook/signin",
                                socialGoogle: String = "https://oauth.theguardian.com/google/signin") extends ViewModel {
  def toMap(implicit messages: Messages) =
    Map("socialFacebook" -> socialFacebook, "socialGoogle" -> socialGoogle)
}

case class ErrorViewModel(id: String, message: String) extends ViewModel {
  def toMap(implicit messages: Messages) =
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

case class SignInViewModel(showPrelude: Boolean = false,
                           errors: Seq[ErrorViewModel] = Seq.empty,
                           email: String = "",
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
      "links" -> links.toMap,
      "actions" -> actions)
}
