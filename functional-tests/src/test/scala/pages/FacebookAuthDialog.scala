package test.pages

import test.util.{LoadablePage, Browser}

class FacebookAuthDialog extends LoadablePage with Browser {
  val url = "https://www.facebook.com/v2.2/dialog/oauth"

  def hasLoaded(): Boolean = pageHasElement(confirmButton)

  def confirm(): Unit = {
    assert(pageHasElement(confirmButton))
    click.on(confirmButton)
  }

  def editProvidedInfo(): Unit = {
    assert(pageHasElement(editInfoLink))
    click.on(editInfoLink)
  }

  private lazy val confirmButton = name("__CONFIRM__")

  // locating by inner text because ID attribute is not fixed by FB (u_0_q, u_0_s,...)
  private lazy val editInfoLink = xpath("//a[contains(text(), 'Edit the info you provide')]")
}
