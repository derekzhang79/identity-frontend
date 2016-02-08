package com.gu.identity.frontend.models

import java.net.URI
import com.gu.identity.frontend.configuration.Configuration

case class ReturnUrl(uri: URI) {
  lazy val url: String = uri.toString
}

object ReturnUrl {

  val domains = List(".theguardian.com", ".code.dev-theguardian.com", ".thegulocal.com")
  val invalidUrlPaths = List("/signin", "/register", "/register/confirm")

  def apply(returnUrl: Option[String], referer: Option[String], configuration: Configuration): ReturnUrl = {

    val default = ReturnUrl(new URI(configuration.identityDefaultReturnUrl))

    returnUrl.map(url => ReturnUrl(new URI(url)))
      .orElse(referer.map(url => ReturnUrl(new URI(url))))
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

  def host(returnUrl: ReturnUrl): String = returnUrl.uri.getHost

  def path(returnUrl: ReturnUrl): String = returnUrl.uri.getPath
}
