package com.gu.identity.service.client

import play.api.mvc.Cookie
import com.gu.identity.frontend.models.TrackingData

trait ApiRequest {
  val method: HttpMethod = GET
  val headers: HttpParameters = Nil
  val url: String
  val parameters: HttpParameters = Nil
  val body: Option[ApiRequestBody] = None
}

trait ApiRequestBody

object ApiRequest {
  def apiKeyHeader(implicit configuration: IdentityClientConfiguration) =
    "X-GU-ID-Client-Access-Token" -> s"Bearer ${configuration.apiKey}"

  def xForwardedForIpHeader(trackingData: TrackingData) =
    trackingData.ipAddress.map("X-Forwarded-For" -> _)

  def commonApiHeaders(trackingData: TrackingData)(implicit configuration: IdentityClientConfiguration): Iterable[(String, String)] =
    Iterable(Some(apiKeyHeader), xForwardedForIpHeader(trackingData)).flatten

  def apiSecureCookieUserHeader(cookie: Cookie) = {
    Iterable("X-GU-ID-FOWARDED-SC-GU-U" -> cookie.value)
  }

  def apiEndpoint(path: String)(implicit configuration: IdentityClientConfiguration): String =
    s"https://${configuration.host}/$path"

  def apiEndpoint(pathComponents: String*)(implicit configuration: IdentityClientConfiguration): String = {
    val path = pathComponents.mkString("/")
    apiEndpoint(path)
  }

  private[client] def encodeBody(params: (String, String)*) = {
    def encode = java.net.URLEncoder.encode(_: String, "UTF8")

    params.map(p => s"${p._1}=${encode(p._2)}").mkString("&")
  }
}

case class UserRequest(url: String, override val headers: HttpParameters) extends ApiRequest

object UserRequest {

  def apply(cookie: Cookie)(implicit configuration: IdentityClientConfiguration): UserRequest = {
    val pathComponents = Seq("user", "me")
    new UserRequest(
      ApiRequest.apiEndpoint(pathComponents: _*),
      Seq(ApiRequest.apiKeyHeader) ++ ApiRequest.apiSecureCookieUserHeader(cookie)
    )
  }
}

