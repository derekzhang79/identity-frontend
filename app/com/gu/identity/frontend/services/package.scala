package com.gu.identity.frontend


package object services {

  sealed trait ServiceError {
    val message: String
  }

  case class ServiceGatewayError(message: String) extends ServiceError
  case class ServiceBadRequest(message: String) extends ServiceError

}
