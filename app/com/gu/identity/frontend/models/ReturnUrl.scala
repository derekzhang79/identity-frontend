package com.gu.identity.frontend.models

import play.api.mvc.{AnyContent, Request}

case class ReturnUrl(url: String)

object ReturnUrl {

  private val urlRegex = """^https?://([^/]+).*$""".r
  val default = ReturnUrl("http://www.theguardian.com")

  def apply(request: Request[AnyContent], returnUrl: Option[String]): ReturnUrl =
    returnUrl.orElse(refererUrl(request)).map(ReturnUrl(_)).getOrElse(default)

  def refererUrl(request: Request[AnyContent]): Option[String] = request.headers.get("Referer")

  private def normaliseReturnUrl(returnUrl: Option[String]) =
    returnUrl
      .filter(validateReturnUrl)
      .getOrElse("https://www.theguardian.com") // default

  private def validateReturnUrl(returnUrl: String) =
    returnUrl match {
      case urlRegex(domain) if domain.endsWith(".theguardian.com") => true
      case _ => false
    }

}
