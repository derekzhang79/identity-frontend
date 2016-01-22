package test.pages

import test.util.{LoadablePage, Browser, Config}

class RegisterConfirm extends LoadablePage with Browser {
  val url = s"""${Config.baseUrl}/register/confirm?returnUrl=${Config.baseUrl}/register"""

  def hasLoaded(): Boolean = pageHasElement(confirmRegistrationButton)

  def confirmRegistration() = clickOn(confirmRegistrationButton)

  private lazy val confirmRegistrationButton = className(s"submit-input")
}
