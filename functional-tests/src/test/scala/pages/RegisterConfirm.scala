package test.pages

import test.util.{LoadablePage, Browser, Config}

class RegisterConfirm extends LoadablePage with Browser {
  val url = s"""${Config.baseUrl}/register/confirm?returnUrl=${Config.baseUrl}/register"""

  def confirmRegistration() = {
    assert(pageHasElement(confirmRegistrationButton))
    click.on(confirmRegistrationButton)
  }

  def hasLoaded(): Boolean = {
    pageHasElement(confirmRegistrationButton)
  }

  private lazy val confirmRegistrationButton = className(s"submit-input")
}
