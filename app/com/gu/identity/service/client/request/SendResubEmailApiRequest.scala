package com.gu.identity.service.client.request

import java.net.URLEncoder

import com.gu.identity.frontend.request.EmailResubscribeRequest
import com.gu.identity.service.client._

case class SendResubEmailApiRequestBody(email: String) extends ApiRequestBody

case class SendResubEmailApiRequest(data: EmailResubscribeRequest)
                                   (implicit configuration: IdentityClientConfiguration) extends ApiRequest {
  override val method = POST
  override val headers = Iterable(
    ApiRequest.apiKeyHeader,
    "Content-Type" -> "application/json")
  override val body = Some(SendResubEmailApiRequestBody(data.email))
  override val url = ApiRequest.apiEndpoint(s"resub-email") + data.returnUrl.map(s => s"?returnUrl=${URLEncoder.encode(s, "UTF-8")}").getOrElse("")
}

