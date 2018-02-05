package com.gu.identity.service.client.request

import com.gu.identity.service.client.{ApiRequest, IdentityClientConfiguration, POST}

case class UserConsentTokenRequest private(override val url: String) extends ApiRequest {
  override val method = POST
}


object UserConsentTokenRequest {
  def apply(token: String, config: IdentityClientConfiguration): UserConsentTokenRequest = {
    val pathComponents = Seq("consent-email", token)
    new UserConsentTokenRequest(ApiRequest.apiEndpoint(pathComponents: _*)(config)) {
      override val headers = List(ApiRequest.apiKeyHeader(config))
    }
  }
}
