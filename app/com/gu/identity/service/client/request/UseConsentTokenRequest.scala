package com.gu.identity.service.client.request

import com.gu.identity.service.client.{ApiRequest, IdentityClientConfiguration, POST}

case class UseConsentTokenRequest private(override val url: String) extends ApiRequest {
  override val method = POST
}


object UseConsentTokenRequest {
  def apply(token: String, config: IdentityClientConfiguration): UseConsentTokenRequest = {
    val pathComponents = Seq("consent-email", token)
    new UseConsentTokenRequest(ApiRequest.apiEndpoint(pathComponents: _*)(config)) {
      override val headers = List(ApiRequest.apiKeyHeader(config))
    }
  }
}
