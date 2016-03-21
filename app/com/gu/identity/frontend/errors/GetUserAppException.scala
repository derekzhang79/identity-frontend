package com.gu.identity.frontend.errors

import com.gu.identity.frontend.errors.ErrorIDs.{GetUserGatewayErrorID, GetUserBadRequestErrorID}
import com.gu.identity.service.client.{ClientGatewayError, ClientBadRequestError, IdentityClientError}


sealed trait GetUserAppException extends AppException

object GetUserAppException {
  def apply(clientError: IdentityClientError): GetUserAppException =
    clientError match {
      case err: ClientBadRequestError => GetUserServiceBadRequestException(clientError)
      case err: ClientGatewayError => GetUserServiceGatewayAppException(clientError)
    }
}

case class GetUserServiceGatewayAppException(
    clientError: IdentityClientError)
  extends ServiceGatewayAppException(clientError)
  with GetUserAppException {

  val id = GetUserGatewayErrorID
}


case class GetUserServiceBadRequestException(
    clientError: IdentityClientError)
  extends ServiceBadRequestAppException(clientError)
  with GetUserAppException {

  val id = GetUserBadRequestErrorID
}
