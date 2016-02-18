package com.gu.identity.frontend.logging
import akka.actor._
import play.api.Logger

trait SuccessfulActionLogger {
  private val system = ActorSystem()
  val successfulActionLogger = system.actorOf(Props[SuccessfulActionLoggingActor])
}

case object SignIn
case object Register

private class SuccessfulActionLoggingActor extends Actor{

  private val logger = Logger(this.getClass)

  override def receive: Receive = {
    case SignIn => {logger.info("Successful Sign In")}
    case Register => {logger.info("Successful Registration")}
    case _ => {logger.info("Unexpected Message received by successful action logging actor.")}
  }
}
