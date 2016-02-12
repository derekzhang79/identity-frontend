package com.gu.identity.frontend.logging
import akka.actor._

trait SuccessfulActionLogger {
  private val system = ActorSystem()
  val successfulActionLogger = system.actorOf(Props[SuccessfulActionLoggingActor])
}

case object SignIn
case object Register

private class SuccessfulActionLoggingActor extends Actor{
  override def receive: Receive = {
    case SignIn => println("signing in")
    case Register => println("registering")
    case _ => println("Unexpected Response")
  }
}
