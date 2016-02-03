package com.gu.identity.frontend.controllers

import com.gu.identity.frontend.logging.Logging
import play.api.mvc.Action
import play.api.mvc.Results._
import play.api.mvc.BodyParsers.parse


class CSPViolationReporter extends Logging{

  def cspReport() = Action(parse.tolerantText) { implicit request =>

    val userAgent = request.headers.get("User-Agent")
    val report = request.body

    logger.error(s"Content Security Violation Error: User Agent: ${userAgent}.  Report: ${report}.")

    Ok("Content Security Policy Violation Logged.")
  }

}
