package com.gu.identity.service.client.request

import com.gu.identity.service.client._

case class UserTypeRequest private(
                                             override val url: String,
                                             private val extraHeaders: HttpParameters = Nil,
                                             private val extraParameters: HttpParameters = Nil)
  extends ApiRequest {
  override val headers = Seq("Content-Type" -> "application/json") ++ extraHeaders
  override val parameters = extraParameters
}

object UserTypeRequest {
  def apply(email: String)(implicit configuration: IdentityClientConfiguration) = {
    val pathComponents = Seq("user", "type", email)
    new UserTypeRequest(
      ApiRequest.apiEndpoint(pathComponents: _*),
      Seq(ApiRequest.apiKeyHeader)
    )
  }

}
