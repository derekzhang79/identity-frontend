package test.pages

import test.util.{Config, LoadablePage, Browser}

class Homepage extends LoadablePage with Browser {
  val url = Config.homepageUrl

  def hasLoaded(): Boolean = pageHasElement(signInName)

  private lazy val signInName = className("js-profile-info")
}
