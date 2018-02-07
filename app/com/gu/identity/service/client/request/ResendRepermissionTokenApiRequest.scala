package com.gu.identity.service.client.request

import com.gu.identity.frontend.request.ResendTokenActionRequestBody
import com.gu.identity.service.client._

case class ResendRepermissionTokenApiRequest(data: ResendTokenActionRequestBody)
                                       (implicit configuration: IdentityClientConfiguration) extends ApiRequest {
  override val method = POST
  override val body = Some(ResendRepermissionFromTokenApiRequestBody(`scopedToken` = data.token))
  override val headers = Iterable(
    ApiRequest.apiKeyHeader,
    "Content-Type" -> "application/json")
  override val url = ApiRequest.apiEndpoint(s"repermission/auth/send-email")
}

case class ResendRepermissionFromTokenApiRequestBody(`scopedToken`: String) extends ApiRequestBody
