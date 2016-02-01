package com.gu.identity.frontend.controllers

import com.gu.identity.frontend.services.GoogleRecaptchaServiceHandler
import play.api.mvc.Result

import scala.concurrent.{ExecutionContext, Future}

class GoogleRecaptchaCheck(googleRecaptchaServiceHandler: GoogleRecaptchaServiceHandler) {
  def apply(
      googleRecaptchaResponse: Option[String],
      errorHandler: => Future[Result])
      (result: => Future[Result])
      (implicit ec: ExecutionContext): Future[Result] ={
    googleRecaptchaResponse match {
      case Some(recaptchaResponseCode) => {
        val isValidResponse = googleRecaptchaServiceHandler.isValidRecaptchaResponse(recaptchaResponseCode)
        isValidResponse.flatMap{
          case true => result
          case false => errorHandler
        }
      }
      case None => {
        result
      }
    }
  }
}
