package com.gu.identity.frontend.controllers

import buildinfo.BuildInfo
import play.api.mvc.{Action, Controller}

class Manifest extends Controller {
  def manifest = Action {
    val data = Map(
      "Build" -> BuildInfo.buildNumber,
      "Commit" -> BuildInfo.gitCommitId
    )

    Ok(data map { case (k, v) => s"$k: $v"} mkString "\n")
  }
}
