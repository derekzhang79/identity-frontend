package com.gu.identity.frontend.errors

import com.gu.identity.frontend.errors.ErrorIDs.{AssignGroupBadRequestErrorID, AssignGroupGatewayErrorID}
import com.gu.identity.service.client.{ClientGatewayError, ClientBadRequestError, IdentityClientError}


sealed trait AssignGroupAppException extends AppException

object AssignGroupAppException {
  def apply(clientError: IdentityClientError): AssignGroupAppException =
    clientError match {
      case err: ClientBadRequestError => AssignGroupServiceBadRequestException(clientError)
      case err: ClientGatewayError => AssignGroupServiceGatewayAppException(clientError)
    }
}

case class AssignGroupServiceGatewayAppException(
    clientError: IdentityClientError)
  extends ServiceGatewayAppException(clientError)
  with AssignGroupAppException {

  val id = AssignGroupGatewayErrorID
}


case class AssignGroupServiceBadRequestException(
    clientError: IdentityClientError)
  extends ServiceBadRequestAppException(clientError)
  with AssignGroupAppException {

  val id = AssignGroupBadRequestErrorID
}
