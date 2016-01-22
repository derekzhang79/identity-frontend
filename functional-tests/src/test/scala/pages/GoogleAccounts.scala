package test.pages

import test.util.{Browser, LoadablePage}
import test.util.user.GoogleTestUser

class GoogleAccounts extends LoadablePage with Browser {
  val url = "https://accounts.google.com/ServiceLogin"

  def hasLoaded(): Boolean = pageHasElement(id("Email"))

  def signIn(): Unit = {
    setValue(emailInput, GoogleTestUser.email)
    clickOn(nextButton)
    setValue(passwordInput, GoogleTestUser.password)
    clickOn(signinButton)
  }

  private lazy val emailInput = id("Email")
  private lazy val nextButton = id("next")
  private lazy val passwordInput = id("Passwd")
  private lazy val signinButton = id("signIn")
}
