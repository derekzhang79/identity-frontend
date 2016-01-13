package com.gu.identity.frontend.views.models

import play.api.i18n.Messages

case class ErrorViewModel(id: String, message: String) extends ViewModel

object ErrorViewModel {

  def apply(id: String): ErrorViewModel = {
    ErrorViewModel(id, getErrorMessage(id))
  }

  val errorMessages = Map(
    "error-gateway" -> "There was a problem signing in; please try again.",
    "error-bad-request" -> "Incorrect email or password; please try again.",
    "error-registration" -> "Sorry an account could not be set up."
  )

  val default: String = "There was an unexpected problem; please try again."

  private def getErrorMessage(id: String) = errorMessages.getOrElse(id, default)
}
