package com.gu.identity.frontend.logging
import akka.actor._
import play.api.Logger

trait MetricsLoggingActor {
  private val system = ActorSystem()
  val successfulActionLogger = system.actorOf(Props[MetricsActor])
}

trait Message {}

object SignIn extends Message
object Register extends Message

private class MetricsActor extends Actor{

  private val logger = Logger(this.getClass)

  override def receive: Receive = {
    case SignIn => {
      SuccessfulActionCloudwatchLogging.putSignIn()
    }
    case Register => {
      SuccessfulActionCloudwatchLogging.putRegister()
    }
    case _ =>  logger.info("Unexpected Message received by successful action logging actor.")
  }
}
