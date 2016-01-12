package test.pages

import test.util.Browser
import org.scalatest.selenium.Page

class Homepage extends Page with Browser {
  val url = "www.theguardian.com"

  def pageHasLoaded(): Boolean = {
    pageHasElement(signInName)
  }

  private lazy val signInName = className("js-profile-info")
}
