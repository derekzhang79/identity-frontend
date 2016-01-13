package com.gu.identity.frontend.filters

import com.gu.identity.frontend.configuration.Configuration
import play.api.mvc.{EssentialAction, EssentialFilter}
import play.filters.headers.{SecurityHeadersConfig, SecurityHeadersFilter => PlaySecurityHeadersFilter}

/**
 * Adapter for Play's SecurityHeadersFilter, allowing overriding of headers.
 *
 * Removes Content Security Policy, as it's set when pages are rendered.
 */
class SecurityHeadersFilter(configuration: Configuration) extends EssentialFilter {

  private val defaultConfig = SecurityHeadersConfig.fromConfiguration(configuration.appConfiguration)

  val augmentedConfig = defaultConfig.copy(contentSecurityPolicy = None)


  val delegate = PlaySecurityHeadersFilter(augmentedConfig)

  override def apply(next: EssentialAction): EssentialAction =
    delegate.apply(next)

}
