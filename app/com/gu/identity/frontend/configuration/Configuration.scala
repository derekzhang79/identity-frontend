package com.gu.identity.frontend.configuration

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth._
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Regions
import play.api.{Configuration => PlayConfiguration}

import scala.util.Try


case class Configuration(
  identityCookieDomain: String,

  identityApiKey: String,
  identityApiHost: String,

  identityProfileBaseUrl: String,

  identityDefaultReturnUrl: String,

  identityFederationApiHost: String,

  omnitureAccount: String,

  recaptchaEnabled: Boolean,
  googleRecaptchaSiteKey: String,
  googleRecaptchaSecretKey: String,

  underlying: PlayConfiguration)


object Configuration {
  def apply(appConfiguration: PlayConfiguration): Configuration = {

    def getString(path: String) =
      appConfiguration.getString(path).getOrElse(sys.error(s"Missing configuration: $path"))

    def getBoolean(path: String) =
      appConfiguration.getBoolean(path).getOrElse(sys.error(s"Missing configuration: $path"))


    Configuration(
      identityCookieDomain = getString("identity.frontend.cookieDomain"),

      identityApiKey = getString("identity.api.key"),
      identityApiHost = getString("identity.api.host"),

      identityFederationApiHost = getString("identity.federation.api.host"),

      identityProfileBaseUrl = getString("identity.frontend.baseUrl"),

      identityDefaultReturnUrl = getString("identity.frontend.defaultReturnUrl"),

      omnitureAccount = getString("omniture.account"),

      googleRecaptchaSiteKey = getString("google.recaptcha.site"),

      googleRecaptchaSecretKey = getString("google.recaptcha.secret"),

      recaptchaEnabled = getBoolean("google.recaptchaEnabled"),

      underlying = appConfiguration
    )
  }


  lazy val testConfiguration = Configuration(
    identityCookieDomain = "dev-theguardian.com",

    identityApiKey = "--test-key--",
    identityApiHost = "idapi.code.dev-theguardian.com",

    identityFederationApiHost = "https://oauth.code.dev-theguardian.com",

    identityProfileBaseUrl = "https://profile.code.dev-theguardian.com",

    identityDefaultReturnUrl = "http://www.theguardian.com",

    omnitureAccount = "--test-omniture-account--",

    googleRecaptchaSiteKey = "--recaptcha-key--",

    googleRecaptchaSecretKey = "--recaptcha-secret-key--",

    recaptchaEnabled = true,

    underlying = PlayConfiguration.empty
  )

  object AWSConfig {
    val credentials: AWSCredentialsProvider = {
      val provider = new AWSCredentialsProviderChain(
        new EnvironmentVariableCredentialsProvider(),
        new SystemPropertiesCredentialsProvider(),
        new ProfileCredentialsProvider(),
        new InstanceProfileCredentialsProvider
      )
      provider.getCredentials
      provider
    }

    val clientConfiguration: ClientConfiguration = new ClientConfiguration()

    val region: Regions = Regions.EU_WEST_1;
  }

  object Environment {
    lazy val stage = System.getProperty("stage", "DEV")
  }
}
