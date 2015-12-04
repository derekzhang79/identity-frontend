package test.util

import java.net.URL
import com.typesafe.config.ConfigFactory
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.remote.{DesiredCapabilities, RemoteWebDriver}
import org.openqa.selenium.{Platform, WebDriver}
import org.slf4j.LoggerFactory
import scala.util.Try

object Config {
  def logger = LoggerFactory.getLogger(this.getClass)

  private val conf = ConfigFactory.load()

  private val baseUrlsByStage = Map(
    "PROD" -> "https://profile-beta.theguardian.com"
  )
  val stage = conf.getString("stage")
  val baseUrl = baseUrlsByStage(stage)

  val testUsersSecret = conf.getString("identity.test.users.secret")

  lazy val driver: WebDriver = {
    Try { new URL(conf.getString("webDriverRemoteUrl")) }.toOption.map { url =>
      val capabilities = DesiredCapabilities.chrome()
      capabilities.setCapability("platform", Platform.WIN8)
      capabilities.setCapability("name", "identity-frontend: https://github.com/guardian/identity-frontend")
      new RemoteWebDriver(url, capabilities)
    }.getOrElse {
      new ChromeDriver()
    }
  }

  def webDriverSessionId(): String = {
    Config.driver match {
      case remoteDriver: RemoteWebDriver => remoteDriver.getSessionId.toString
      case _ => {
        logger.error("Unknown Web Driver session ID")
        "unknown"
      }
    }
  }

  def debug() = {
    conf.root().render()
  }

  def printSummary(): Unit = {
    logger.info("Functional Test Configuration")
    logger.info("=============================")
    logger.info(s"Stage: ${stage}")
    logger.info(s"Identity Frontend: ${Config.baseUrl}")
    logger.info(s"WebDriver Session ID = ${Config.webDriverSessionId}")
  }
}
