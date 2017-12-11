package com.gu.identity.frontend.experiments

import play.api.mvc.Request
import play.mvc.Http

abstract case class Experiment(
  name: String,
  description: String,
  defaultStatus: Boolean = false
) {

  def isActive()(implicit request: Request[Any]) : Boolean = {
    defaultStatus || request.cookies.get(headerName()).isDefined
  }

  def headerName(): String = "x-gu-identity-"+name

}

