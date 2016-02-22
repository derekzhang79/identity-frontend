package com.gu.identity.frontend.logging
import akka.actor._
import play.api.Logger

trait MetricsLoggingActor {

  def logSuccessfulRegister(): Unit = {
    MetricsLoggingActor.logSuccessfulRegister()
  }

  def logSuccessfulSignin(): Unit = {
    MetricsLoggingActor.logSuccessfulSignin()
  }
}

private sealed trait Message {}

private object SignIn extends Message
private object Register extends Message

private object MetricsLoggingActor {
  private val system = ActorSystem()
  val successfulActionLogger = system.actorOf(Props[MetricsActor])

  def logSuccessfulRegister(): Unit = {
    successfulActionLogger ! Register
  }

  def logSuccessfulSignin(): Unit = {
    successfulActionLogger ! SignIn
  }
}

private class MetricsActor extends Actor with Logging{

  override def receive: Receive = {
    case SignIn => {
      SuccessfulActionCloudwatchLogging.putSignIn()
    }
    case Register => {
      SuccessfulActionCloudwatchLogging.putRegister()
    }
    case _ =>  logger.info("Unexpected Message received by metrics actor.")
  }
}
