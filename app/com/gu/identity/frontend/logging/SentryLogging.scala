package com.gu.identity.frontend.logging

import com.getsentry.raven.RavenFactory
import com.getsentry.raven.dsn.Dsn
import com.getsentry.raven.logback.SentryAppender
import com.gu.identity.frontend.configuration.Configuration
import ch.qos.logback.classic.filter.ThresholdFilter
import ch.qos.logback.classic.{Logger, LoggerContext}
import org.slf4j.Logger.ROOT_LOGGER_NAME
import org.slf4j.LoggerFactory
import buildinfo.BuildInfo
import javax.inject.Singleton

@Singleton
class SentryLogging(config: Configuration) {
  val dsn = new Dsn(config.sentryDsnScala)
  play.api.Logger.info(s"Initialising Sentry logging for project ${dsn.getProjectId}")

  val tagsString = BuildInfo.toMap.map { case (key, value) => s"$key:$value" }.mkString(",")

  val filter = new ThresholdFilter { setLevel("ERROR") }
  filter.start()

  val sentryAppender = new SentryAppender(RavenFactory.ravenInstance(dsn)) {
    addFilter(filter)
    setTags(tagsString)
    setContext(LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext])
  }
  sentryAppender.start()
  LoggerFactory.getLogger(ROOT_LOGGER_NAME).asInstanceOf[Logger].addAppender(sentryAppender)
}
