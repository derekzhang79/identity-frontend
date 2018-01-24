package com.gu.identity.service.client.request

import com.gu.identity.frontend.request.ResendConsentTokenActionRequestBody
import com.gu.identity.service.client._

case class ResendConsentTokenApiRequest(data: ResendConsentTokenActionRequestBody)
                                           (implicit configuration: IdentityClientConfiguration) extends ApiRequest {
  override val method = POST
  override val headers = Iterable(
    ApiRequest.apiKeyHeader,
    "Content-Type" -> "application/json")
  override val url = ApiRequest.apiEndpoint(s"consent-email/resend/${data.token}")
}

