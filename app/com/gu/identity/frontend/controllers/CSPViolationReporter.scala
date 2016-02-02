package com.gu.identity.frontend.controllers

import play.api.mvc.Action
import play.api.mvc.Results._


class CSPViolationReporter {

  def cspReport() = Action{ request =>
    println(request.body)
    Ok("Reporting Error")
  }

}
