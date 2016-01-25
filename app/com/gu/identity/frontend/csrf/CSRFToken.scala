package com.gu.identity.frontend.csrf

import play.api.mvc.RequestHeader
import play.filters.csrf.CSRF.{Token, getToken}

case class CSRFToken private(fieldName: String, value: String)

object CSRFToken {
  def apply(config: CSRFConfig, token: Token): CSRFToken =
    CSRFToken(config.tokenName, token.value)

  def fromRequest(config: CSRFConfig, request: RequestHeader): Option[CSRFToken] =
    getToken(request, config.underlying)
      .map(CSRFToken(config, _))

}
