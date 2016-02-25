package com.gu.identity.service.client.request

import com.gu.identity.frontend.controllers.ResetPasswordData
import com.gu.identity.service.client._

case class SendResetPasswordEmailApiRequest(data: ResetPasswordData)(implicit configuration: IdentityClientConfiguration) extends ApiRequest {
  override val method = GET
  override val headers = Iterable(ApiRequest.apiKeyHeader)
  override val url = ApiRequest.apiEndpoint("pwd-reset/send-password-reset-email")
  override val parameters = Seq("type" -> "reset", "email-address" -> data.email)
}
