package com.gu.identity.frontend.services

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.logging.Logging
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.Future
import scala.util.control.NonFatal

import play.api.libs.concurrent.Execution.Implicits.defaultContext

class GoogleRecaptchaServiceHandler(ws: WSClient, configuration: Configuration) extends Logging{

  implicit val googleResponseFormat = Json.format[GoogleResponse]

  def isValidRecaptchaResponse(captchaResponseCode: String): Future[Boolean] = {
    val googleResponse = getRecaptchaResponseFromGoogle(captchaResponseCode)
    googleResponse.map {
      case GoogleResponse(true, _) => true
      case GoogleResponse(false, errors) => {
        logger.warn(s"Google Recaptcha failed to authenticate ${errors}")
        false
      }
    }.recover{
      case NonFatal(ex) => {
        logger.warn(s"Unexpected error from google recaptcha: ${ex.getMessage}", ex)
        false
      }
    }

  }

  def getRecaptchaResponseFromGoogle(captchaResponseCode: String): Future[GoogleResponse] = {
    ws.url("https://www.google.com/recaptcha/api/siteverify").post(
      Map("secret" -> Seq(configuration.googleRecaptchaSecretKey), "response" -> Seq(captchaResponseCode))
    ).map{
      googleResponse => {
        val body = googleResponse.body
        Json.parse(body).as[GoogleResponse]
      }
    }
  }
}

case class GoogleResponse(success: Boolean, errorCodes: Option[List[String]])
