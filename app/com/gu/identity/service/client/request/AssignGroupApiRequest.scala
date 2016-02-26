package com.gu.identity.service.client.request

import com.gu.identity.service.client._
import play.api.mvc.Cookie

case class AssignGroupApiRequest(url: String, override val headers: HttpParameters) extends ApiRequest {
  override val method: HttpMethod = POST
}

object AssignGroupApiRequest {

  private def getPathComponents(group: String) = Seq("user", "me", "group", group)

  def apply(group: String, cookie: Cookie)(implicit configuration: IdentityClientConfiguration): AssignGroupApiRequest = {
    val pathComponents = getPathComponents(group)
    new AssignGroupApiRequest(
      ApiRequest.apiEndpoint(pathComponents: _*),
      Seq(ApiRequest.apiKeyHeader) ++ ApiRequest.apiSecureCookieUserHeader(cookie)
    )
  }
}
