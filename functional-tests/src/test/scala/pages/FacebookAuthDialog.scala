package test.pages

import test.util.{LoadablePage, Browser}

class FacebookAuthDialog extends LoadablePage with Browser {
  val url = "https://www.facebook.com/v2.2/dialog/oauth"

  def hasLoaded(): Boolean = pageHasElement(confirmButton)

  def confirm(): Unit = {
    assert(pageHasElement(confirmButton))
    click.on(confirmButton)
  }

  def editProvidedInfo(): Unit = click.on(editInfoLink)

  private lazy val confirmButton = name("__CONFIRM__")
  private lazy val editInfoLink = id("u_0_q")
}
