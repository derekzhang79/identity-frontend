package test.util

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import scala.util.{Failure, Success, Try}

object Config {
  def logger = LoggerFactory.getLogger(this.getClass)

  private val conf = ConfigFactory.load()

  private val baseUrlsByStage = Map(
    "PROD" -> "https://profile.theguardian.com",
    "CODE" -> "https://profile.code.dev-theguardian.com",
    "DEV"  -> "https://profile.thegulocal.com")

  private val idApiUrlsByStage = Map(
    "PROD" -> "idapi.theguardian.com",
    "CODE" -> "idapi.code.dev-theguardian.com",
    "DEV"  -> "idapi.thegulocal.com")

  private val homepageUrlsByStage = Map(
    "PROD" -> "https://www.theguardian.com",
    "CODE" -> "http://m.code.dev-theguardian.com",
    "DEV"  -> "http://m.thegulocal.com")

  var stage = conf.getString("stage")

  var baseUrl = baseUrlsByStage(stage)

  var homepageUrl = homepageUrlsByStage(stage)

  var idApiUrl = idApiUrlsByStage(stage)

  val testUsersSecret = conf.getString("identity.test.users.secret")

  val webDriverRemoteUrl = Try(conf.getString("webDriverRemoteUrl")) match {
    case Success(url) => url
    case Failure(e) => ""
  }

  object FacebookAppCredentials {
    val id = conf.getString(s"facebook.app.$stage.id")
    val secret = conf.getString(s"facebook.app.$stage.secret")
  }

  object GoogleTestUserCredentials {
    val email = conf.getString("google.test.user.email")
    val password = conf.getString("google.test.user.password")
    val name = conf.getString("google.test.user.name")
  }

  object ResetPasswordEmail {
    val to = conf.getString("password.reset.email.to")
    val from = conf.getString("password.reset.email.from")
  }


  def debug() = conf.root().render()

  def printSummary(): Unit = {
    logger.info("Functional Test Configuration")
    logger.info("=============================")
    logger.info(s"Stage: ${stage}")
    logger.info(s"Identity Frontend: ${Config.baseUrl}")
    logger.info(s"Identity API: ${Config.idApiUrl}")
    logger.info(s"Homepage: ${Config.homepageUrl}")
    logger.info(s"Screencast = https://saucelabs.com/tests/${Driver.sessionId}")
  }
}
