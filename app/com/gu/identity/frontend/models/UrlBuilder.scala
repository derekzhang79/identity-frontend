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
    apply(baseUrl, returnUrl, skipConfirmation = None, clientId = None, group = None, skipThirdPartyLandingPage = None)

  def apply(baseUrl: String, returnUrl: ReturnUrl, clientId: Option[ClientID]): String =
    apply(baseUrl, returnUrl, skipConfirmation = None, clientId, group = None, skipThirdPartyLandingPage = None)

  def apply(baseUrl: String, returnUrl: ReturnUrl, skipConfirmation: Option[Boolean], clientId: Option[ClientID]): String =
    apply(baseUrl, returnUrl, skipConfirmation, clientId, group = None, skipThirdPartyLandingPage = None)

  def apply(baseUrl: String, returnUrl: ReturnUrl, skipConfirmation: Option[Boolean], clientId: Option[ClientID], group: Option[String]): String =
    apply(baseUrl, returnUrl, skipConfirmation, clientId, group, skipThirdPartyLandingPage = None)

  def apply(baseUrl: String, returnUrl: ReturnUrl, skipConfirmation: Option[Boolean], clientId: Option[ClientID], group: Option[String], skipThirdPartyLandingPage: Option[Boolean]): String =
    apply(baseUrl, buildParams(returnUrl, skipConfirmation, clientId, group, skipThirdPartyLandingPage))

  def apply(call: Call, params: Seq[(String, String)]): String =
    apply(call.url, params)

  def apply(call: Call, returnUrl: ReturnUrl, clientId: Option[ClientID]): String =
    apply(call, returnUrl, None, clientId, None)

  def apply(call: Call, returnUrl: ReturnUrl, skipConfirmation: Option[Boolean], clientId: Option[ClientID], group: Option[String]): String =
    apply(call.url, returnUrl, skipConfirmation, clientId, group, skipThirdPartyLandingPage = None)

  def apply(baseUrl: String, call: Call): String = s"$baseUrl${call.url}"

  def buildThirdPartyReturnUrl(
      returnUrl: ReturnUrl,
      skipConfirmation: Option[Boolean],
      skipThirdPartyLandingPage: Boolean,
      clientId: Option[ClientID],
      group: GroupCode,
      configuration: Configuration): ReturnUrl = {

    val baseThirdPartyReturnUrl = configuration.identityProfileBaseUrl + "/agree/" + group.getCodeValue
    val thirdPartyReturnUrl = apply(baseThirdPartyReturnUrl, returnUrl, skipConfirmation, clientId, group = None, Some(skipThirdPartyLandingPage))
    ReturnUrl(Some(thirdPartyReturnUrl), configuration)
  }

  def buildOauthReturnUrl(
      baseUrl: String,
      returnUrl: ReturnUrl,
      skipConfirmation: Option[Boolean],
      clientId: Option[ClientID],
      group: GroupCode,
      configuration: Configuration): String = {

    val thirdPartyReturnUrl = buildThirdPartyReturnUrl(returnUrl, skipConfirmation, skipThirdPartyLandingPage = true, clientId, group, configuration)
    apply(baseUrl, thirdPartyReturnUrl, skipConfirmation, clientId, Some(group.getCodeValue), skipThirdPartyLandingPage = None)
  }

  private def buildParams(
                           returnUrl: ReturnUrl,
                           skipConfirmation: Option[Boolean],
                           clientId: Option[ClientID],
                           group: Option[String],
                           skipThirdPartyLandingPage: Option[Boolean]): Seq[(String, String)] =
    Seq(
      Some(returnUrl).filterNot(_.isDefault).map("returnUrl" -> _.url),
      skipConfirmation.map("skipConfirmation" -> _.toString),
      clientId.map("clientId" -> _.id),
      group.map("group" -> _),
      skipThirdPartyLandingPage.map("skipThirdPartyLandingPage" -> _.toString)
    ).flatten
}
