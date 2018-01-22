package com.gu.identity.frontend.errors

import com.gu.identity.frontend.errors.ErrorIDs._
import com.gu.identity.service.client.{ClientBadRequestError, ClientGatewayError, IdentityClientError}

sealed trait ResendConsentTokenExeption extends AppException

object ResendConsentTokenExeption {
  def apply(clientError: IdentityClientError): ResendConsentTokenExeption =
    clientError match {
      case err: ClientBadRequestError => ResendConsentTokenBadRequestExeption(clientError)
      case err: ClientGatewayError => ResendConsentTokenServiceGatewayExeption(clientError)
    }
}


case class ResendConsentTokenServiceGatewayExeption(clientError: IdentityClientError)
  extends ServiceGatewayAppException(clientError)
    with ResendConsentTokenExeption {

  val id = ResendConsentEmailGatewayErrorID
}

case class ResendConsentTokenBadRequestExeption(clientError: IdentityClientError)
  extends BadRequestAppException
    with ResendConsentTokenExeption {

  val id = ResendConsentEmailBadRequestID
}

case class ResendConsentTokenBadTokenExeption(message: String)
  extends AbstractAppException(message)
    with BadRequestAppException
    with ResendConsentTokenExeption {

  val id = ResendConsentEmailBadTokenRequestId
}

case class ResendConsentTokenBadAppExeption(message: String)
  extends AbstractAppException(message)
    with BadRequestAppException
    with ResendConsentTokenExeption {

  val id = ResendConsentEmailGatewayErrorID
}
