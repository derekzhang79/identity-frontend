package test.util

import org.openqa.selenium.support.ui.{ExpectedCondition, ExpectedConditions, WebDriverWait}
import org.openqa.selenium.By
import org.scalatest.selenium.WebBrowser
import scala.util.Try

trait Browser extends WebBrowser {

  lazy implicit val driver = Driver()

  private val timeOutSec = 30

  def pageHasText(text: String): Boolean = {
    waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), text))
  }

  def pageHasElement(q: Query): Boolean = {
    waitUntil(ExpectedConditions.visibilityOfElementLocated(q.by))
  }

  def pageHasUrl(urlFraction: String): Boolean = {
    waitUntil(ExpectedConditions.urlContains(urlFraction))
  }

  def elementHasText(q: Query, text: String): Boolean = {
    waitUntil(ExpectedConditions.textToBePresentInElementLocated(q.by, text))
  }

  def waitUntil[T](pred: ExpectedCondition[T]) = {
    Try(new WebDriverWait(driver, timeOutSec).until(pred)).isSuccess
  }
}
