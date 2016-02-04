package com.gu.identity.frontend.models

import java.net.URI
import scala.util.Try

case class ReturnUrl(url: String)

object ReturnUrl {

  val default = ReturnUrl("http://www.theguardian.com")
  val domains = List(".theguardian.com", ".code.dev-theguardian.com", ".thegulocal.com")
  val invalidUrlPaths = List("/signin", "/register", "/register/confirm")

  def apply(returnUrl: Option[String], referer: Option[String]): ReturnUrl = {
    returnUrl.map(ReturnUrl(_))
      .orElse(referer.map(ReturnUrl(_)))
      .filter(validDomain(_))
      .filter(validUrlPath(_))
      .getOrElse(default)
  }

  def validDomain(returnUrl: ReturnUrl): Boolean = {
    val hostname = host(returnUrl)
    domains.exists(s".$hostname".endsWith(_))
  }

  def validUrlPath(returnUrl: ReturnUrl): Boolean = {
    val urlPath = path(returnUrl)
    !invalidUrlPaths.contains(urlPath)
  }

  def host(returnUrl: ReturnUrl): String = Try(new URI(returnUrl.url)).map(_.getHost).getOrElse("")

  def path(returnUrl: ReturnUrl): String = Try(new URI(returnUrl.url)).map(_.getPath).getOrElse("")
}
