package com.gu.identity.frontend.analytics

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import com.gu.identity.frontend.analytics.client.{MeasurementProtocolClient, MeasurementProtocolRequest, RegisterEventRequest, SigninEventRequest}
import com.gu.identity.frontend.logging.Logging

private sealed trait Message
private sealed trait Event extends Message {
  val request: MeasurementProtocolRequest
}

private object Terminate extends Message
private case class SignIn(request: SigninEventRequest) extends Event
private case class Register(request: RegisterEventRequest) extends Event

class AnalyticsEventActor(eventActor: ActorRef) {

  def sendSuccessfulRegister(registerEventRequest: RegisterEventRequest) = {
    eventActor ! Register(registerEventRequest)
  }

  def sendSuccessfulSignin(signinEventRequest: SigninEventRequest) = {
    eventActor ! SignIn(signinEventRequest)
  }

  def terminateActor() = {
    eventActor ! PoisonPill
  }
}

object EventActor {
  def getProps(measurementProtocolClient: MeasurementProtocolClient): Props =
    Props(new EventActor(measurementProtocolClient))
}

private class EventActor(measurementProtocolClient: MeasurementProtocolClient) extends Actor with Logging {

  override def receive: Receive = {
    case SignIn(event) => measurementProtocolClient.sendSuccessfulSigninEvent(event)
    case Register(event) => measurementProtocolClient.sendSuccessfulRegisterEvent(event)
    case _ =>  logger.warn("Unexpected event received by analytics event actor.")
  }
}
