package com.gu.identity.frontend.models

import com.gu.identity.frontend.configuration.Configuration
import play.api.mvc.Call
import com.gu.identity.frontend.controllers._

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

  def apply(baseUrl: String, returnUrl: ReturnUrl): String =
    apply(baseUrl, returnUrl, skipConfirmation = None, clientId = None, group = None)

  def apply(baseUrl: String, returnUrl: ReturnUrl, clientId: Option[ClientID]): String =
    apply(baseUrl, returnUrl, skipConfirmation = None, clientId, group = None)

  def apply(baseUrl: String, returnUrl: ReturnUrl, skipConfirmation: Option[Boolean], clientId: Option[ClientID]): String =
    apply(baseUrl, buildParams(returnUrl, skipConfirmation, clientId, group = None))

  def apply(baseUrl: String, returnUrl: ReturnUrl, skipConfirmation: Option[Boolean], clientId: Option[ClientID], group: Option[String]): String =
    apply(baseUrl, buildParams(returnUrl, skipConfirmation, clientId, group))


  def apply(call: Call, params: Seq[(String, String)]): String =
    apply(call.url, params)

  def apply(call: Call, returnUrl: ReturnUrl, clientId: Option[ClientID]): String =
    apply(call, returnUrl, None, clientId, None)

  def apply(call: Call, returnUrl: ReturnUrl, skipConfirmation: Option[Boolean], clientId: Option[ClientID], group: Option[String]): String =
    apply(call.url, returnUrl, skipConfirmation, clientId, group)

  def buildThirdPartyReturnUrl(baseUrl: String, returnUrl: ReturnUrl, skipConfirmation: Option[Boolean], clientId: Option[ClientID], group: GroupCode, configuration: Configuration): String = {
    val relativeThirdPartyUrl = routes.ThirdPartyTsAndCs.confirmAction(group.getCodeValue, None, clientId.map(_.id), Some(true))
    val absoluteThirdPartyUrl = configuration.identityProfileBaseUrl + relativeThirdPartyUrl


    val constructedReturnUrl = apply(absoluteThirdPartyUrl, returnUrl, skipConfirmation, clientId, Some(group.getCodeValue))
    val absoluteThirdPartyReturnUrl = ReturnUrl(Some(constructedReturnUrl), configuration)
    apply(baseUrl, absoluteThirdPartyReturnUrl)
  }


  private def buildParams(returnUrl: ReturnUrl, skipConfirmation: Option[Boolean], clientId: Option[ClientID], group: Option[String]): Seq[(String, String)] =
    Seq(
      Some(returnUrl).filterNot(_.isDefault).map("returnUrl" -> _.url),
      skipConfirmation.map("skipConfirmation" -> _.toString),
      clientId.map("clientId" -> _.id),
      group.map("group" -> _)
    ).flatten
}
