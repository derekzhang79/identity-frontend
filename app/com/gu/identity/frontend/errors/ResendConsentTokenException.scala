package com.gu.identity.frontend.errors

import com.gu.identity.frontend.errors.ErrorIDs._
import com.gu.identity.service.client.{ClientBadRequestError, ClientGatewayError, IdentityClientError}

sealed trait ResendConsentTokenException extends AppException

object ResendConsentTokenExeption {
  def apply(clientError: IdentityClientError): ResendConsentTokenException =
    clientError match {
      case _: ClientBadRequestError => ResendConsentTokenBadRequestException(clientError)
      case _: ClientGatewayError => ResendConsentTokenServiceGatewayExeption(clientError)
    }
}


case class ResendConsentTokenServiceGatewayExeption(clientError: IdentityClientError)
  extends ServiceGatewayAppException(clientError)
    with ResendConsentTokenException {

  val id = ResendConsentEmailGatewayErrorID
}

case class ResendConsentTokenBadRequestException(clientError: IdentityClientError)
  extends BadRequestAppException
    with ResendConsentTokenException {

  val id = ResendConsentEmailBadRequestID
}

case class ResendConsentTokenBadTokenException(message: String)
  extends AbstractAppException(message)
    with BadRequestAppException
    with ResendConsentTokenException {

  val id = ResendConsentEmailBadTokenRequestId
}

case class ResendConsentTokenBadRequestAppException(message: String)
  extends AbstractAppException(message)
    with BadRequestAppException
    with ResendConsentTokenException {

  val id = ResendConsentEmailGatewayErrorID
}
