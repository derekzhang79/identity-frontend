package com.gu.identity.frontend.logging

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.auth._
import com.amazonaws.regions.{Regions, Region}
import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsyncClient
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest

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

object SuccessfulActionCloudwatchLogging {

  private lazy val cloudwatch = {
    val client = new AmazonCloudWatchAsyncClient(AWSConfig.credentials, AWSConfig.clientConfiguration)
    client.setEndpoint(Region.getRegion(Regions.EU_WEST_1).getServiceEndpoint(com.amazonaws.regions.ServiceAbbreviations.CloudWatch))
    client
  }

  def putSignIn(): Unit = {
    println("putting signin")
//    val request = new PutMetricDataRequest()
//    cloudwatch.putMetricDataAsync(request)
  }

  def putRegister(): Unit = {
    println("putting register")
//    val request = new PutMetricDataRequest()
//    cloudwatch.putMetricDataAsync(request)
  }
}
