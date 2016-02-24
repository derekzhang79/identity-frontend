package com.gu.identity.service.client

import com.gu.identity.frontend.controllers.RegisterRequest
import com.gu.identity.frontend.models.{ClientRegistrationIp, TrackingData}

sealed trait ApiRequest {
  val method: HttpMethod = GET
  val headers: HttpParameters = Nil
  val url: String
  val parameters: HttpParameters = Nil
  val body: Option[ApiRequestBody] = None
}

sealed trait ApiRequestBody

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

case class AuthenticateCookiesRequest private(
    url: String,
    override val body: Option[AuthenticateCookiesRequestBody],
    private val extraHeaders: HttpParameters = Nil,
    private val extraParameters: HttpParameters = Nil)
extends ApiRequest {

  override val method = POST
  override val headers = Seq("Content-Type" -> "application/x-www-form-urlencoded") ++ extraHeaders
  override val parameters = Seq("format" -> "cookies") ++ extraParameters
}

case class AuthenticateCookiesRequestBody(email: String, password: String) extends ApiRequestBody

object AuthenticateCookiesRequest {
  private val emailRegex = "^.+@.+$".r

  private def isValidEmail(email: String): Boolean =
    emailRegex.findFirstIn(email).isDefined

  private def isValidPassword(password: String): Boolean =
    password.nonEmpty


  def endpoint(implicit configuration: IdentityClientConfiguration) =
    ApiRequest.apiEndpoint("auth")


  def apply(email: Option[String], password: Option[String], rememberMe: Boolean, trackingData: TrackingData)(implicit configuration: IdentityClientConfiguration): Either[BadRequest, AuthenticateCookiesRequest] =
    (email, password) match {
      case (Some(e), Some(p)) if isValidEmail(e) && isValidPassword(p) => Right {
        apply(AuthenticateCookiesRequestBody(e, p), rememberMe, trackingData)
      }
      case _ => Left {
        BadRequest("Invalid request")
      }
    }


  def apply(body: AuthenticateCookiesRequestBody, rememberMe: Boolean, trackingData: TrackingData)(implicit configuration: IdentityClientConfiguration): AuthenticateCookiesRequest = {
    lazy val extraHeaders = ApiRequest.apiKeyHeaders
    lazy val extraParams = Seq(
      "persistent" -> rememberMe.toString
    ) ++ trackingData.parameters

    AuthenticateCookiesRequest(endpoint, Some(body), extraHeaders, extraParams)
  }

}

case class RegisterApiRequest(url: String, extraHeaders: HttpParameters = Nil, trackingData: TrackingData, override val body: Option[ApiRequestBody]) extends ApiRequest {
  override val method = POST
  override val headers = Seq("Content-Type" -> "application/json") ++ extraHeaders
  override val parameters = trackingData.parameters
}

object RegisterApiRequest {
  def apply(request: RegisterRequest, clientIp: ClientRegistrationIp, trackingData: TrackingData)(implicit configuration: IdentityClientConfiguration): RegisterApiRequest ={
    RegisterApiRequest(
      ApiRequest.apiEndpoint("user"),
      body = Some(RegisterRequestBody(
        request.email,
        request.password,
        RegisterRequestBodyPublicFields(request.username),
        RegisterRequestBodyPrivateFields(
          firstName = request.firstName,
          secondName = request.lastName,
          registrationIp = clientIp.ip
        ),
        RegisterRequestBodyStatusFields(
          receiveGnmMarketing = request.receiveGnmMarketing,
          receive3rdPartyMarketing = request.receive3rdPartyMarketing
        )
      )),
      extraHeaders = ApiRequest.apiKeyHeaders,
      trackingData = trackingData
    )
  }
}

case class RegisterRequestBody(
    primaryEmailAddress: String,
    password: String,
    publicFields: RegisterRequestBodyPublicFields,
    privateFields: RegisterRequestBodyPrivateFields,
    statusFields: RegisterRequestBodyStatusFields)
  extends ApiRequestBody

case class RegisterRequestBodyPublicFields(
    username: String)

case class RegisterRequestBodyPrivateFields(
    firstName: String,
    secondName: String,
    registrationIp: String)

case class RegisterRequestBodyStatusFields(
    receiveGnmMarketing: Boolean,
    receive3rdPartyMarketing: Boolean)
