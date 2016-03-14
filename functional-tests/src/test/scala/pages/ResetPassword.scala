package test.pages

import test.util.{Browser, Config, LoadablePage}

class ResetPassword extends LoadablePage with Browser {
  val url = s"${Config.baseUrl}/reset"

  def hasLoaded(): Boolean = pageHasElement(resetPasswordButton)

  def setEmailAddress(email: String) = emailField(emailAddress).value = email

  def submit() = clickOn(resetPasswordButton)

  private lazy val resetPasswordButton = cssSelector("[data-test-id=\"reset-password-btn\"]")
  private lazy val emailAddress = id("email-address")
}
