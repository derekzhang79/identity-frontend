package com.gu.identity.frontend.models

import java.net.URI
import scala.util.Try

case class ReturnUrl(url: String)

object ReturnUrl {

  val default = ReturnUrl("http://www.theguardian.com")
  val domains = List(".theguardian.com", ".code.dev-theguardian.com", ".thegulocal.com")
  val profile = List("profile.theguardian.com", "profile.code.dev-theguardian.com", "profile-origin.thegulocal.com")

  def apply(returnUrl: Option[String], referer: Option[String]): ReturnUrl = {
    returnUrl.map(ReturnUrl(_))
      .orElse(referer.map(ReturnUrl(_)))
      .filter(validDomain(_))
      .filterNot(profileDomain(_))
      .getOrElse(default)
  }

  def validDomain(returnUrl: ReturnUrl): Boolean = {
    val hostname = host(returnUrl)
    domains.exists(s".$hostname".endsWith(_))
  }

  def profileDomain(returnUrl: ReturnUrl): Boolean = {
    val hostname = host(returnUrl)
    profile.exists(s".$hostname".endsWith(_))
  }

  def host(returnUrl: ReturnUrl): String = Try(new URI(returnUrl.url)).map(_.getHost).getOrElse("")

}
