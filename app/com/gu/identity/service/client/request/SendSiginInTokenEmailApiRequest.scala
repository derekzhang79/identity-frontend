package com.gu.identity.service.client.request

import java.net.URLEncoder

import com.gu.identity.frontend.request.EmailSignInRequest
import com.gu.identity.service.client._

case class SendSiginInTokenEmailApiRequestBody(email: String) extends ApiRequestBody

case class SendSiginInTokenEmailApiRequest(data: EmailSignInRequest)
                                          (implicit configuration: IdentityClientConfiguration) extends ApiRequest {
  override val method = POST
  override val headers = Iterable(
    ApiRequest.apiKeyHeader,
    "Content-Type" -> "application/json")
  override val body = Some(SendSiginInTokenEmailApiRequestBody(data.email))
  override val url = ApiRequest.apiEndpoint(s"auth/email") + data.returnUrl.map(s => s"?returnUrl=${URLEncoder.encode(s, "UTF-8")}").getOrElse("")
}

