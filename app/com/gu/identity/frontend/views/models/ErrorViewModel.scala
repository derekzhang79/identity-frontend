package com.gu.identity.frontend.views.models

import play.api.i18n.Messages

case class ErrorViewModel(id: String, message: String) extends ViewModel

object ErrorViewModel {

  def apply(id: String): ErrorViewModel = {
    ErrorViewModel(id, getErrorMessage(id))
  }

  val errorMessages = Map(
    "signin-error-gateway" -> "There was a problem signing in; please try again.",
    "signin-error-bad-request" -> "Incorrect email or password; please try again.",
    "register-error-gateway" -> "There was a problem creating your account; please try again.",
    "register-error-bad-request" -> "One or more inputs was not valid; please try again.",
    "register-error-username-in-use" -> "The username you selected is already in use; please choose another one.",
    "register-error-email-in-use" -> "This email is already in use; please check you do not already have an account.",
    "error-registration" -> "Sorry an account could not be set up; please try again."
  )

  val default: String = "There was an unexpected problem; please try again."

  private def getErrorMessage(id: String) = errorMessages.getOrElse(id, default)
}
