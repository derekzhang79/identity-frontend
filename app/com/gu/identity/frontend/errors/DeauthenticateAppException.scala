package com.gu.identity.frontend.errors

import com.gu.identity.frontend.errors.ErrorIDs._
import com.gu.identity.service.client.{ClientBadRequestError, ClientGatewayError, IdentityClientError}

sealed trait DeauthenticateAppException extends AppException

object DeauthenticateAppException {
  def apply(clientError: IdentityClientError): DeauthenticateAppException =
    clientError match {
      case err: ClientBadRequestError => DeauthenticateServiceBadRequestException(clientError)
      case err: ClientGatewayError => DeauthenticateServiceGatewayAppException(clientError)
    }
}


case class DeauthenticateServiceGatewayAppException(
    clientError: IdentityClientError)
  extends ServiceGatewayAppException(clientError)
  with DeauthenticateAppException {

  val id = DeauthenticateGatewayErrorID
}

case class ConsentTokenAppException(
 clientError: IdentityClientError
) extends ServiceGatewayAppException(clientError) {
  override val id = ConsentTokenGatewayErrorID
}

case class ConsentTokenUnauthorizedException(
 clientError: IdentityClientError
) extends ServiceGatewayAppException(clientError) {
  override val id = UnauthorizedConsentTokenErrorID
}

case class RepermissionTokenUnauthorizedException(
 clientError: IdentityClientError
 ) extends ServiceGatewayAppException(clientError) {
  override val id = UnauthorizedRepermissionTokenErrorID
}

case class RepermissionTokenAppException(
 clientError: IdentityClientError
) extends ServiceGatewayAppException(clientError) {
  override val id = RepermissionTokenGatewayErrorID
}


case class DeauthenticateServiceBadRequestException(
    clientError: IdentityClientError)
  extends ServiceBadRequestAppException(clientError)
  with DeauthenticateAppException {

  val id = DeauthenticateBadRequestErrorID
}
