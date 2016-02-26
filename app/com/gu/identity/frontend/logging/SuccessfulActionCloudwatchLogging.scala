package com.gu.identity.frontend.logging

import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.regions.{Regions, Region}
import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsyncClient
import com.amazonaws.services.cloudwatch.model.{Dimension, MetricDatum, PutMetricDataRequest}
import com.gu.identity.frontend.configuration.Configuration._

object LoggingAsyncHandler extends AsyncHandler[PutMetricDataRequest, Void] with Logging {
  def onError(exception: Exception) {
    logger.error(s"CloudWatch PutMetricDataRequest error: ${exception.getMessage}}")
  }

  def onSuccess(request: PutMetricDataRequest, result: Void) {
    logger.debug("CloudWatch PutMetricDataRequest - success")
  }
}

object SuccessfulActionCloudwatchLogging {

  private lazy val cloudwatch = {
    val client = new AmazonCloudWatchAsyncClient(AWSConfig.credentials, AWSConfig.clientConfiguration)
    client.setEndpoint(AWSConfig.region.getServiceEndpoint(com.amazonaws.regions.ServiceAbbreviations.CloudWatch))
    client
  }

  private lazy val stageDimension = new Dimension().withName("Stage").withValue(Environment.stage)

  private def createRequest(namespace: String, metricName: String) = {
    new PutMetricDataRequest()
      .withNamespace(namespace)
      .withMetricData(
        new MetricDatum()
          .withMetricName(metricName)
          .withUnit("Count")
          .withValue(1d)
          .withDimensions(stageDimension)
      )
  }

  def putSignIn(): Unit = {
    val request = createRequest("SuccessfulSignIns", "SuccessfulSignIn")
    cloudwatch.putMetricDataAsync(request, LoggingAsyncHandler)
  }

  def putRegister(): Unit = {
    val request = createRequest("SuccessfulRegistrations", "SuccessfulRegistration")
    cloudwatch.putMetricDataAsync(request, LoggingAsyncHandler)
  }
}
