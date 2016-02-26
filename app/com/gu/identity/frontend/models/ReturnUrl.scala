package com.gu.identity.frontend.models

import java.net.URI
import com.gu.identity.frontend.configuration.Configuration

import scala.util.Try

case class ReturnUrl private[models](
    uri: URI,
    isDefault: Boolean = false) {

  lazy val url: String = uri.toString
}

object ReturnUrl {

  val domains = List(".theguardian.com", ".code.dev-theguardian.com", ".thegulocal.com")
  val invalidUrlPaths = List("/signin", "/register", "/register/confirm")

  def apply(returnUrl: Option[String], configuration: Configuration): ReturnUrl =
    apply(returnUrl, referer = None, configuration)

  def apply(returnUrl: Option[String], referer: Option[String], configuration: Configuration): ReturnUrl =
    returnUrl
      .flatMap(uriOpt)
      .orElse(referer.flatMap(uriOpt))
      .filter(validDomain)
      .filter(validUrlPath)
      .map(uri => ReturnUrl(uri))
      .getOrElse {
        default(configuration)
      }

  def default(configuration: Configuration) = {
    val defaultUri = uri(configuration.identityDefaultReturnUrl).getOrElse {
      sys.error("Invalid defaultReturnUrl specified in configuration")
    }
    ReturnUrl(defaultUri, isDefault = true)
  }

  /**
   * Parse a URI from a url string, ensure URISyntaxException is caught
   * by wrapping in a Try.
   */
  def uri(url: String): Try[URI] =
    Try(new URI(url))

  def uriOpt(url: String): Option[URI] =
    uri(url).toOption

  private[models] def validDomain(uri: URI): Boolean = {
    val hostname = uri.getHost
    domains.exists(s".$hostname".endsWith(_))
  }

  private[models] def validUrlPath(uri: URI): Boolean = {
    val urlPath = uri.getPath
    !invalidUrlPaths.contains(urlPath)
  }
}
