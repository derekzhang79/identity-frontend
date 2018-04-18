package com.gu.identity.service.client.request

import com.gu.identity.service.client._

case class ResubTokenRequest private(override val url: String) extends ApiRequest
final case class ResubTokenRequestBody(token: String) extends ApiRequestBody

object ResubTokenRequest {
  def apply(token: String, config: IdentityClientConfiguration): ResubTokenRequest = {
    val pathComponents = Seq("auth", "resub-email")
    new ResubTokenRequest(ApiRequest.apiEndpoint(pathComponents: _*)(config) + "?format=cookies") {
      override val method: HttpMethod = POST
      override val headers = Iterable(ApiRequest.apiKeyHeader(config))
      override val body = Some(ResubTokenRequestBody(token))
    }
  }
}
