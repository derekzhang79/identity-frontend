package com.gu.identity.frontend.models

import play.api.mvc.Call

object UrlBuilder {

  private def encode = java.net.URLEncoder.encode(_: String, "UTF8")

  def apply(baseUrl: String, params: Seq[(String, String)]): String =
    params.headOption match {
      case None => baseUrl
      case _ => {
        val paramString = params.map {
          case (key, value) => s"$key=${encode(value)}"
        }.mkString("&")

        s"$baseUrl?$paramString"
      }
    }

  def apply(baseUrl: String, returnUrl: ReturnUrl, clientId: Option[ClientID]): String =
    apply(baseUrl, returnUrl, skipConfirmation = None, clientId)

  def apply(baseUrl: String, returnUrl: ReturnUrl, skipConfirmation: Option[Boolean], clientId: Option[ClientID]): String =
    apply(baseUrl, buildParams(returnUrl, skipConfirmation, clientId))


  def apply(call: Call, params: Seq[(String, String)]): String =
    apply(call.url, params)

  def apply(call: Call, returnUrl: ReturnUrl, clientId: Option[ClientID]): String =
    apply(call, returnUrl, None, clientId)

  def apply(call: Call, returnUrl: ReturnUrl, skipConfirmation: Option[Boolean], clientId: Option[ClientID]): String =
    apply(call.url, returnUrl, skipConfirmation, clientId)


  private def buildParams(returnUrl: ReturnUrl, skipConfirmation: Option[Boolean], clientId: Option[ClientID]): Seq[(String, String)] =
    Seq(
      Some(returnUrl).filter(isDefaultReturnUrl).map("returnUrl" -> _.url),
      skipConfirmation.map("skipConfirmation" -> _.toString),
      clientId.map("clientId" -> _.id)
    ).flatten

  private def isDefaultReturnUrl(returnUrl: ReturnUrl): Boolean = {
    returnUrl.url == "http://www.theguardian.com"
  }
}
