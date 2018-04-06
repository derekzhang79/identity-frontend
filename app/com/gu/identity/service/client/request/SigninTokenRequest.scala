package com.gu.identity.service.client.request

import com.gu.identity.service.client.{ApiRequest, IdentityClientConfiguration}

case class SigninTokenRequest private(override val url: String) extends ApiRequest

object SigninTokenRequest {
  def apply(token: String, config: IdentityClientConfiguration): SigninTokenRequest = {
    val pathComponents = Seq("auth", token)
    new SigninTokenRequest(ApiRequest.apiEndpoint(pathComponents: _*)(config) + "?format=cookies") {
      override val headers = Iterable(ApiRequest.apiKeyHeader(config))
    }
  }
}
