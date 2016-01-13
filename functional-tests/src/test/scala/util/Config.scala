package test.util

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import scala.util.{Failure, Success, Try}

object Config {
  def logger = LoggerFactory.getLogger(this.getClass)

  private val conf = ConfigFactory.load()

  private val baseUrlsByStage = Map(
    "PROD" -> "https://profile.theguardian.com"
  )
  val stage = conf.getString("stage")

  val baseUrl = baseUrlsByStage(stage)

  val testUsersSecret = conf.getString("identity.test.users.secret")

  val webDriverRemoteUrl = Try(conf.getString("webDriverRemoteUrl")) match {
    case Success(url) => url
    case Failure(e) => ""
  }

  object FacebookCredentials {
    val email = conf.getString("facebook.test.email")
    val password = conf.getString("facebook.test.password")
    val name = conf.getString("facebook.test.name")
  }

  def debug() = conf.root().render()

  def printSummary(): Unit = {
    logger.info("Functional Test Configuration")
    logger.info("=============================")
    logger.info(s"Stage: ${stage}")
    logger.info(s"Identity Frontend: ${Config.baseUrl}")
    logger.info(s"Screencast = https://saucelabs.com/tests/${Driver.sessionId}")
  }
}
