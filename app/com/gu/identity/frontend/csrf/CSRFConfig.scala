package com.gu.identity.frontend.csrf

import play.api.Configuration
import play.filters.csrf.{CSRFConfig => PlayCSRFConfig}


case class CSRFConfig private[csrf](
    underlying: PlayCSRFConfig,
    enabled: Boolean = true) {
  val tokenName = underlying.tokenName
}

object CSRFConfig {
  /** Default config */
  def apply(): CSRFConfig =
    apply(enabled = true)

  def apply(enabled: Boolean): CSRFConfig =
    CSRFConfig(PlayCSRFConfig(), enabled)

  /** Config from Play configuration */
  def apply(configuration: Configuration): CSRFConfig =
    apply(configuration, enabled = true)

  def apply(configuration: Configuration, enabled: Boolean): CSRFConfig =
    CSRFConfig(PlayCSRFConfig.fromConfiguration(configuration))

  /** Disabled config for tests */
  lazy val disabled =
    apply(enabled = false)
}
