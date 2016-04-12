package com.gu.identity.frontend.views.models
import com.gu.identity.frontend.errors.ErrorID
import com.gu.identity.frontend.errors.ErrorIDs._
case class RegisterErrorViewModel(usernameErrors: Seq[ErrorViewModel], emailErrors: Seq[ErrorViewModel], passwordErrors: Seq[ErrorViewModel], otherErrors: Seq[ErrorViewModel])

object RegisterErrorViewModel {

  val errorIdToFields: Map[ErrorID, String] = Map(
    RegisterActionInvalidUsernameErrorID -> "usernameError",
    RegisterUsernameConflictErrorID -> "usernameError",
    RegisterActionInvalidEmailErrorID -> "emailError",
    RegisterEmailConflictErrorID -> "emailError",
    RegisterActionInvalidPasswordErrorID -> "passwordError"
  )
  lazy val errorFieldByKey: Map[String, String] =
    errorIdToFields.map {
      case (k, v) => k.key -> v
    }

  private def getErrorField(id: String) = errorFieldByKey.get(id)

  def mapErrorToErrorType(errors: Map[String, Option[String]], errorType: Option[String]) = {
    errors.filter {
      case (k, v) => v == errorType
      case _ => false
    }.map {
      case (k, v) => ErrorViewModel(k)
    }.toSeq
  }

  def apply(ids: Seq[String]): RegisterErrorViewModel = {
    val errors = ids.map(id => id -> getErrorField(id)).toMap
    val usernameErrors = mapErrorToErrorType(errors, Some("usernameError"))
    val emailErrors = mapErrorToErrorType(errors, Some("emailError"))
    val passwordErrors = mapErrorToErrorType(errors, Some("passwordError"))
    val otherErrors = mapErrorToErrorType(errors, None)
    RegisterErrorViewModel(usernameErrors, emailErrors, passwordErrors, otherErrors)
  }
}
