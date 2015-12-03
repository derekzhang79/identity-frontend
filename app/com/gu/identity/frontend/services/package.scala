package com.gu.identity.frontend


package object services {

  sealed trait ServiceError {
    val message: String
    val description: Option[String]
  }

  case class ServiceGatewayError(message: String, description: Option[String] = None) extends ServiceError
  case class ServiceBadRequest(message: String, description: Option[String] = None) extends ServiceError

}
