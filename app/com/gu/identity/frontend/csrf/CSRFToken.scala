package com.gu.identity.frontend.csrf

import play.api.mvc.RequestHeader
import play.filters.csrf.{CSRF, CSRFConfig}

case class CSRFToken(fieldName: String, value: String)

object CSRFToken {
  def apply(config: CSRFConfig, token: CSRF.Token): CSRFToken =
    CSRFToken(config.tokenName, token.value)

  def fromRequest(config: CSRFConfig, request: RequestHeader): Option[CSRFToken] =
    CSRF.getToken(request, config)
      .map(CSRFToken(config, _))

}
