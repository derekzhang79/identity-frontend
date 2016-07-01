package test.util

import org.openqa.selenium.support.ui.{ExpectedCondition, ExpectedConditions, WebDriverWait}
import org.openqa.selenium.support.ui.ExpectedConditions.or
import org.openqa.selenium.By
import org.scalatest.selenium.WebBrowser
import scala.util.Try

trait Browser extends WebBrowser {

  lazy implicit val driver = Driver()

  private val timeOutSec = 30

  case class MissingPageElementException(q: Query)
    extends Exception(s"Could not find WebElement with locator: ${q.queryString}")

  case class MissingPageElementABException(queries: Query*) extends Throwable {
    override def getMessage: String =
      s"Could not find WebElement using the following locators: ${queries.map(q => q.queryString).mkString(", ")}"
  }

  def pageHasText(text: String): Boolean =
    waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), text))

  def pageHasElement(q: Query): Boolean =
    waitUntil(ExpectedConditions.visibilityOfElementLocated(q.by))

  def pageHasElementOr(queries: Query*): Boolean =
    waitUntilOr(queries.map(q => ExpectedConditions.visibilityOfElementLocated(q.by)))

  def pageHasUrl(urlFraction: String): Boolean =
    waitUntil(ExpectedConditions.urlContains(urlFraction))

  def elementHasText(q: Query, text: String): Boolean =
    waitUntil(ExpectedConditions.textToBePresentInElementLocated(q.by, text))

  private def waitUntil[T](pred: ExpectedCondition[T]) =
    Try(new WebDriverWait(driver, timeOutSec).until(pred)).isSuccess

  private def waitUntilOr[T](pred: Seq[ExpectedCondition[T]]) =
    Try(new WebDriverWait(driver, timeOutSec).until(or(pred: _*))).isSuccess

  def clickOn(q: Query): Unit = {
    if (pageHasElement(q))
      click.on(q)
    else
      throw new MissingPageElementException(q)
  }

  def clickOnOr(queries: Query*): Unit =
    if (pageHasElementOr(queries: _*))
      queries.foreach(q => Try(click.on(q)))
    else throw new MissingPageElementABException(queries: _*)

  def setValue(q: Query, value: String): Unit = {
    if (pageHasElement(q))
      q.webElement.sendKeys(value)
    else
      throw new MissingPageElementException(q)
  }
}
