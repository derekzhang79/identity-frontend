package com.gu.identity.frontend.errors

import com.gu.identity.frontend.errors.ErrorIDs.{ConsentTokenGatewayErrorID, DeauthenticateBadRequestErrorID, DeauthenticateGatewayErrorID}
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


case class DeauthenticateServiceBadRequestException(
    clientError: IdentityClientError)
  extends ServiceBadRequestAppException(clientError)
  with DeauthenticateAppException {

  val id = DeauthenticateBadRequestErrorID
}
