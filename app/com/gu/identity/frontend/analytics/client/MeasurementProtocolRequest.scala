package com.gu.identity.frontend.analytics.client

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.request.{RegisterActionRequestBody, SignInActionRequestBody}
import play.api.mvc.Request


trait MeasurementProtocolRequestBody[T] {
  def apply(request: Request[T], config: Configuration): String = {
    val params = commonBodyParameters(
      request.cookies.get("_ga").get.value, // TODO: use the actual client ID (pass from client)
      request.remoteAddress,
      request.headers.get("User-Agent").getOrElse(""),
      request.acceptLanguages.headOption.map(_.language).getOrElse(""),
      request.host + request.uri,
      config.gaUID
    ) ++ extraBodyParams

    encodeBody(params: _*)
  }

  protected val extraBodyParams: Seq[(String, String)] = Seq()

  private def commonBodyParameters(
      clientId: String,
      ipAddress: String,
      userAgent: String,
      userLanguage: String,
      url: String,
      gaUID: String): Seq[(String, String)] =
    Seq(
      "v" -> "1",
      "tid" -> gaUID,
      "cid" -> clientId,
      "t" -> "event",
      "uip" -> ipAddress,
      "ua" -> userAgent,
      "de" -> "UTF-8",
      "ul" -> userLanguage,
      "dl" -> url,
      "ec" -> "identity",
      "cd3" -> "profile.theguardian.com",
      "cd4" -> userAgent,
      "cd5" -> url
    )

  private def encodeBody(params: (String, String)*) = {
    def encode = java.net.URLEncoder.encode(_: String, "UTF8")

    params.map(p => s"${p._1}=${encode(p._2)}").mkString("&")
  }
}

trait MeasurementProtocolRequest {
  val url: String = s"https://www.google-analytics.com/collect"
  val body: String
}

private object SigninEventRequestBody extends MeasurementProtocolRequestBody[SignInActionRequestBody] {
  override val extraBodyParams = Seq(
    "ea" -> "SigninSuccessful",
    "el" -> "RegularSignin",
    "cm2" -> "1"
  )
}

case class SigninEventRequest(request: Request[SignInActionRequestBody], config: Configuration) extends MeasurementProtocolRequest {
  override val body = SigninEventRequestBody(request, config)
}

private object RegisterEventRequestBody extends MeasurementProtocolRequestBody[RegisterActionRequestBody] {
  override val extraBodyParams = Seq(
    "ea" -> "RegisterSuccessful",
    "el" -> "RegularRegistration",
    "cm1" -> "1"
  )
}

case class RegisterEventRequest(request: Request[RegisterActionRequestBody], config: Configuration) extends MeasurementProtocolRequest {
  override val body = RegisterEventRequestBody(request, config)
}
