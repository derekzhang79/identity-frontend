package test.pages

import test.util.{LoadablePage, Browser}

class FacebookAuthDialog extends LoadablePage with Browser {
  val url = "https://www.facebook.com/v2.2/dialog/oauth"

  def hasLoaded(): Boolean = pageHasElement(confirmButton)

  def confirm(): Unit = clickOn(confirmButton)

  def editProvidedInfo(): Unit = clickOnOr(editInfoLinkA, editInfoLinkB, editInfoLinkC)

  private val confirmButton = name("__CONFIRM__")

  // Multiple editInfo links due to Facebook performing A/B testing
  private val editInfoLinkA = id("u_0_q")
  private val editInfoLinkB = id("u_0_p")
  private val editInfoLinkC = id("u_0_m")
}
