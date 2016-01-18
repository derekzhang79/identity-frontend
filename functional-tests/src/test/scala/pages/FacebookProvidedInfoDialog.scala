package test.pages

import test.util.{Browser, LoadablePage}

class FacebookProvidedInfoDialog extends LoadablePage with Browser {
  val url = "https://www.facebook.com/v2.2/dialog/oauth"

  def hasLoaded(): Boolean = pageHasElement(fieldSet)

  def uncheckEmailPermission(): Unit = checkbox(cssSelector("input[value='email']")).clear()

  private lazy val fieldSet = id("u_0_s")
}
