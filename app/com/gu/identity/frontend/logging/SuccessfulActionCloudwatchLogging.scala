package com.gu.identity.frontend.logging

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.auth._
import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.regions.{Regions, Region}
import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsyncClient
import com.amazonaws.services.cloudwatch.model.{MetricDatum, PutMetricDataRequest}

import scala.util.Try

object AWSConfig{
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

  val clientConfiguration: ClientConfiguration = {
    val config = new ClientConfiguration()
    for {
      proxyHost <- Some(System.getProperty("http.proxyHost")).filterNot(_ == null)
      proxyPort <- Try (Integer.parseInt(System.getProperty("http.proxyPort"))).toOption
    } yield config.withProxyHost(proxyHost).withProxyPort(proxyPort)
    config
  }
}

trait LoggingAsyncHandler extends AsyncHandler[PutMetricDataRequest, Void] with Logging {
  def onError(exception: Exception) {
    logger.error(s"CloudWatch PutMetricDataRequest error: ${exception.getMessage}}")
  }

  def onSuccess(request: PutMetricDataRequest, result: Void) {
    logger.debug("CloudWatch PutMetricDataRequest - success")
  }
}

object LoggingAsyncHandler extends LoggingAsyncHandler

object SuccessfulActionCloudwatchLogging {

  private lazy val cloudwatch = {
    val client = new AmazonCloudWatchAsyncClient(AWSConfig.credentials, AWSConfig.clientConfiguration)
    client.setEndpoint(Region.getRegion(Regions.EU_WEST_1).getServiceEndpoint(com.amazonaws.regions.ServiceAbbreviations.CloudWatch))
    client
  }

  def putSignIn(): Unit = {
    val request = new PutMetricDataRequest()
      .withNamespace("SuccessfulSignIns")
      .withMetricData(
        new MetricDatum()
          .withMetricName("SuccessfulSignIn")
          .withUnit("Count")
      )
    cloudwatch.putMetricDataAsync(request, LoggingAsyncHandler)
  }

  def putRegister(): Unit = {
    val request = new PutMetricDataRequest()
      .withNamespace("SuccessfulRegistrations")
      .withMetricData(
        new MetricDatum()
          .withMetricName("SuccessfulRegistration")
          .withUnit("Count")
      )
    cloudwatch.putMetricDataAsync(request, LoggingAsyncHandler)
  }
}
