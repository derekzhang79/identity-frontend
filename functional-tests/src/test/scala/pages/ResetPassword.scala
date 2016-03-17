package test.pages

import test.util.{Browser, Config, LoadablePage}

class ResetPassword extends LoadablePage with Browser {
  val url = s"${Config.baseUrl}/reset"

  def hasLoaded(): Boolean = pageHasElement(resetPasswordButton)

  def setEmailAddress(email: String) = emailField(emailAddress).value = email

  def submit() = clickOn(resetPasswordButton)

  private lazy val resetPasswordButton = id("reset-password_submit")
  private lazy val emailAddress = id("reset-password_field_email")
}
