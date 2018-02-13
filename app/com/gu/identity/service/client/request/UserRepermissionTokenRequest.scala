package com.gu.identity.service.client.request

import com.gu.identity.service.client.{ApiRequest, HttpParameters, IdentityClientConfiguration, POST}

case class UserRepermissionTokenRequest private(override val url: String) extends ApiRequest {
  override val method = POST
}


object UserRepermissionTokenRequest {
  def apply(token: String, config: IdentityClientConfiguration): UserRepermissionTokenRequest = {
    val pathComponents = Seq("repermission/auth")
    new UserRepermissionTokenRequest(ApiRequest.apiEndpoint(pathComponents: _*)(config)) {
      override val headers = List(ApiRequest.apiKeyHeader(config))
      override val parameters: HttpParameters = List("token" -> token)
    }
  }
}
