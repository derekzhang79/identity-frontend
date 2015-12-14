package com.gu.identity.frontend.filters

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.views.models.LayoutViewModel
import play.api.mvc.{EssentialAction, EssentialFilter}
import play.filters.headers.{SecurityHeadersConfig, SecurityHeadersFilter => PlaySecurityHeadersFilter}

/**
 * Adapter for Play's SecurityHeadersFilter, allowing overriding of headers.
 *
 * Required to specify hashes of inlined scripts at runtime.
 */
class SecurityHeadersFilter(configuration: Configuration) extends EssentialFilter {

  private val defaultConfig = SecurityHeadersConfig.fromConfiguration(configuration.appConfiguration)

  val CSP_SELF_DOMAIN = "'self'"
  val CSP_DATA_PROTOCOL = "data:"

  val allowedHostsForImages = Seq(
    "https://hits-secure.theguardian.com",
    "https://sb.scorecardresearch.com",
    "https://ophan.theguardian.com"
  )

  val allowedHostsForScripts = Seq(
    "https://j.ophan.co.uk"
  )

  val inlinedScripts = Seq(
    LayoutViewModel(configuration).inlineJsConfig
  )

  val hashesForInlineScripts = inlinedScripts.map(s => toCSPShaDefinition(s.sha256))

  val csp = Map(
    "default-src" -> Seq(CSP_SELF_DOMAIN),
    "script-src" -> (Seq(CSP_SELF_DOMAIN) ++ hashesForInlineScripts ++ allowedHostsForScripts),
    "img-src" -> (Seq(CSP_SELF_DOMAIN, CSP_DATA_PROTOCOL) ++ allowedHostsForImages),
    "font-src" -> Seq(CSP_SELF_DOMAIN, CSP_DATA_PROTOCOL)
  )

  val augmentedConfig = defaultConfig.copy(contentSecurityPolicy = Some(toCSPHeader(csp)))


  val delegate = PlaySecurityHeadersFilter(augmentedConfig)

  override def apply(next: EssentialAction): EssentialAction =
    delegate.apply(next)


  private def toCSPHeader(csp: Map[String, Seq[String]]) =
    csp.map {
      case (key, value) => s"$key ${value.mkString(" ")}"
    }.mkString("; ")

  private def toCSPShaDefinition(in: String) =
    s"'sha256-$in'"
}
