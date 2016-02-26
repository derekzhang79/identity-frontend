package com.gu.identity.service.client.request

import com.gu.identity.service.client._
import play.api.mvc.Cookie

case class UserApiRequest(url: String, override val headers: HttpParameters) extends ApiRequest

object UserApiRequest {

  def apply(cookie: Cookie)(implicit configuration: IdentityClientConfiguration): UserApiRequest = {
    val pathComponents = Seq("user", "me")
    new UserApiRequest(
      ApiRequest.apiEndpoint(pathComponents: _*),
      Seq(ApiRequest.apiKeyHeader) ++ ApiRequest.apiSecureCookieUserHeader(cookie)
    )
  }
}
