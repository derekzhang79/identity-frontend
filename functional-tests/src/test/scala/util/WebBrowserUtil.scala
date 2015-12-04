package test.util

import java.net.URL
import java.util.concurrent.TimeUnit
import org.openqa.selenium.support.ui.{ExpectedCondition, ExpectedConditions, WebDriverWait}
import org.openqa.selenium.{JavascriptExecutor, By, Cookie}
import org.scalatest.selenium.WebBrowser
import scala.util.{Success, Failure, Try}
import scala.collection.JavaConverters._

trait WebBrowserUtil { this: WebBrowser =>

  lazy implicit val driver = Config.driver

  protected def resetDriver() = {
    driver.get("about:about")
    go.to(Config.baseUrl)
    driver.manage().deleteAllCookies()
    driver.manage().timeouts().implicitlyWait(timeOut, TimeUnit.SECONDS)
  }

  protected def pageHasText(text: String): Boolean = {
    waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), text))
  }

  protected def pageHasElement(q: Query): Boolean = {
    waitUntil(ExpectedConditions.visibilityOfElementLocated(q.by))
  }

  protected def pageContainsUrl(urlFraction: String): Boolean = {
    waitUntil(ExpectedConditions.urlContains(urlFraction))
  }

  protected def currentHost: String = new URL(currentUrl).getHost

  protected def cookiesSet: Set[Cookie] = driver.manage().getCookies.asScala.toSet

  private def stopWindowLoad() = {
    val js = driver.asInstanceOf[JavascriptExecutor]
    js.executeScript("window.stop();")
  }

  private def waitUntil[T](pred: ExpectedCondition[T]) = {
    Try(new WebDriverWait(driver, timeOut).until(pred)) match {
      case Success(_) => true
      case Failure(e) => { // Retry once with half the timeout.
        stopWindowLoad()
        Try(new WebDriverWait(driver, timeOut/2).until(pred)).isSuccess
      }
    }
  }

  private val timeOut = 60
}
