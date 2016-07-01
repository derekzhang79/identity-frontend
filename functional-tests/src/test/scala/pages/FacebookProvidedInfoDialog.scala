package test.pages

import org.openqa.selenium.JavascriptExecutor
import test.util.{Browser, LoadablePage}

class FacebookProvidedInfoDialog extends LoadablePage with Browser {
  val url = "https://www.facebook.com/v2.2/dialog/oauth"

  def hasLoaded(): Boolean = pageHasElement(confirmButton)

  def uncheckEmailPermission() {
//     checkbox(cssSelector("input[value='email']")).clear()
    driver.asInstanceOf[JavascriptExecutor].executeScript("document.querySelector(\"input[value='email']\").click()")
  }

  private val confirmButton = name("__CONFIRM__")
}
