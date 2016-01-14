package test.pages

import test.util.{LoadablePage, Browser}

class Homepage extends LoadablePage with Browser {
  val url = "www.theguardian.com"

  def hasLoaded(): Boolean = {
    pageHasElement(signInName)
  }

  private lazy val signInName = className("js-profile-info")
}
