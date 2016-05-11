package com.gu.identity.frontend.logging
import akka.actor._
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}

trait MetricsLoggingActor {

  val logSuccessfulRegister = () => {
    MetricsLoggingActor.logSuccessfulRegister()
  }

  val logSuccessfulSignin = () => {
    MetricsLoggingActor.logSuccessfulSignin()
  }

  val logSuccessfulSmartLockSignin = () => {
    MetricsLoggingActor.logSuccessfulSmartLockSignin()
  }

  def terminateActor()(implicit executionContext: ExecutionContext): Future[Unit] = {
    MetricsLoggingActor.terminateActor().map(_ => ())
  }
}

private sealed trait Message {}

private object SignIn extends Message
private object SmartLockSignIn extends Message
private object Register extends Message
private object Terminate extends Message

private object MetricsLoggingActor {
  private val system = ActorSystem()
  val successfulActionLogger = system.actorOf(Props[MetricsActor])

  def logSuccessfulRegister(): Unit = {
    successfulActionLogger ! Register
  }

  def logSuccessfulSignin(): Unit = {
    successfulActionLogger ! SignIn
  }

  def logSuccessfulSmartLockSignin(): Unit = {
    successfulActionLogger ! SmartLockSignIn
  }

  def terminateActor(): Future[Terminated] = {
    successfulActionLogger ! PoisonPill
    system.terminate()
  }
}

private class MetricsActor extends Actor with Logging{

  override def receive: Receive = {
    case SignIn => {
      SuccessfulActionCloudwatchLogging.putSignIn()
    }
    case SmartLockSignIn => {
      SuccessfulActionCloudwatchLogging.putSmartLockSignIn()
    }
    case Register => {
      SuccessfulActionCloudwatchLogging.putRegister()
    }
    case _ =>  logger.info("Unexpected Message received by metrics actor.")
  }
}
