package com.gu.identity.service.client

import com.gu.identity.frontend.controllers.RegisterRequest
import com.gu.identity.frontend.models.TrackingData

sealed trait ApiRequest {
  val method: HttpMethod = GET
  val headers: HttpParameters = Nil
  val url: String
  val parameters: HttpParameters = Nil
  val body: Option[ApiRequestBody] = None
}

object ApiRequest {
  def apiKeyHeaders(implicit configuration: IdentityClientConfiguration) =
    Iterable("X-GU-ID-Client-Access-Token" -> s"Bearer ${configuration.apiKey}")

  def apiEndpoint(path: String)(implicit configuration: IdentityClientConfiguration) =
    s"https://${configuration.host}/$path"

  private[client] def encodeBody(params: (String, String)*) = {
    def encode = java.net.URLEncoder.encode(_: String, "UTF8")

    params.map(p => s"${p._1}=${encode(p._2)}").mkString("&")
  }
}

case class AuthenticateCookiesRequest(url: String, email: String, password: String, rememberMe: Boolean, trackingData: TrackingData, extraHeaders: HttpParameters = Nil) extends ApiRequest {
  override val method = POST
  override val headers = Seq("Content-Type" -> "application/x-www-form-urlencoded") ++ extraHeaders
  override val parameters = Seq("format" -> "cookies", "persistent" -> rememberMe.toString) ++ trackingData.parameters
  override val body = Some(AuthenticateCookiesRequestBody(email, password))
}

case class AuthenticateCookiesRequestBody(email: String, password: String) extends ApiRequestBody

object AuthenticateCookiesRequest {
  private val emailRegex = "^.+@.+$".r

  private def isValidEmail(email: String): Boolean =
    emailRegex.findFirstIn(email).isDefined

  private def isValidPassword(password: String): Boolean =
    password.nonEmpty

  def from(email: Option[String], password: Option[String], rememberMe: Boolean, trackingData: TrackingData)(implicit configuration: IdentityClientConfiguration): Either[BadRequest, AuthenticateCookiesRequest] =
    (email, password) match {
      case (Some(e), Some(p)) if isValidEmail(e) && isValidPassword(p) => Right(AuthenticateCookiesRequest(ApiRequest.apiEndpoint("auth"), e, p, rememberMe, trackingData, ApiRequest.apiKeyHeaders))
      case _ => Left(BadRequest("Invalid request"))
    }

}

trait ApiRequestBody

case class RegisterApiRequest(url: String, extraHeaders: HttpParameters = Nil, override val body: Option[ApiRequestBody]) extends ApiRequest {
  override val method = POST
  override val headers = Seq("Content-Type" -> "application/json") ++ extraHeaders
  override val parameters = Seq("format" -> "cookies")
}

object RegisterApiRequest {
  def apply(request: RegisterRequest, clientIp: String)(implicit configuration: IdentityClientConfiguration): RegisterApiRequest ={
    RegisterApiRequest(
      ApiRequest.apiEndpoint("user"),
      body = Some(RegisterRequestBody(
        request.email,
        request.password,
        RegisterRequestBodyPublicFields(request.username),
        RegisterRequestBodyPrivateFields(
          request.firstName,
          request.lastName,
          request.receiveGnmMarketing,
          request.receive3rdPartyMarketing,
          clientIp
        )
      )),
      extraHeaders = ApiRequest.apiKeyHeaders
    )
  }
}

case class RegisterRequestBody(primaryEmailAddress: String, password: String, publicFields: RegisterRequestBodyPublicFields, privateFields: RegisterRequestBodyPrivateFields) extends ApiRequestBody

case class RegisterRequestBodyPublicFields(username: String)

case class RegisterRequestBodyPrivateFields(firstName: String, lastName: String, receiveGnmMarketing: Boolean, receive3rdPartyMarketing: Boolean,  registrationIp: String)
