package com.gu.identity.frontend.test

import org.scalatest._
import play.api.{Application, Play}


trait SimpleAppPerSuite extends SuiteMixin { this: Suite =>

  lazy val app: Application = SimpleFakeApplication()

  abstract override def run(testName: Option[String], args: Args): Status = {
    Play.start(app)
    try {
      val newConfigMap = args.configMap + ("org.scalatestplus.play.app" -> app)
      val newArgs = args.copy(configMap = newConfigMap)
      val status = super.run(testName, newArgs)
      status.whenCompleted { _ => Play.stop(app) }
      status
    }
    catch { // In case the suite aborts, ensure the app is stopped
      case ex: Throwable =>
        Play.stop(app)
        throw ex
    }
  }
}
