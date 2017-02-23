package com.gu.identity.frontend.logging
import akka.actor._

private sealed trait Message {}

private object SignIn extends Message
private object SmartLockSignIn extends Message
private object Register extends Message
private object Terminate extends Message

class MetricsLoggingActor(metricsActor: ActorRef) {

  def logSuccessfulRegister() = {
    metricsActor ! Register
  }

  def logSuccessfulSignin() = {
    metricsActor ! SignIn
  }

  def logSuccessfulSmartLockSignin() = {
    metricsActor ! SmartLockSignIn
  }

  def terminateActor() = {
    metricsActor ! PoisonPill
  }
}

class MetricsActor extends Actor with Logging {

  override def receive: Receive = {
    case SignIn => SuccessfulActionCloudwatchLogging.putSignIn()
    case SmartLockSignIn => SuccessfulActionCloudwatchLogging.putSmartLockSignIn()
    case Register => SuccessfulActionCloudwatchLogging.putRegister()
    case _ =>  logger.info("Unexpected Message received by metrics actor.")
  }
}
