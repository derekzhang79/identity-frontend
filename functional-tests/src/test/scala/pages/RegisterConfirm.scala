package test.pages

import test.util.{LoadablePage, Browser, Config}

class RegisterConfirm extends LoadablePage with Browser {
  val url = s"${Config.baseUrl}/register/confirm?returnUrl=${Config.baseUrl}/test-return-test"

  def hasLoaded(): Boolean = pageHasUrl("verify-email")

  def isSignupFlow(): Boolean = pageHasUrl("isSignupFlow")

  def hasPassedReturnUrl(): Boolean = pageHasUrl("test-return-test")

}
