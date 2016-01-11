package test.pages

import test.util.Browser
import org.scalatest.selenium.{Page, WebBrowser}

class Homepage extends Page with WebBrowser with Browser {
  val url = "www.theguardian.com"

  def userDisplayName: String = {
    assert(pageHasElement(signIn))
    signIn.element.text
  }

  def pageHasLoaded(): Boolean = {
    pageHasElement(signIn)
  }

  private lazy val signIn = className("js-profile-info")
}
