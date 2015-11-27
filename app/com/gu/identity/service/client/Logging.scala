package com.gu.identity.service.client

import org.slf4j.LoggerFactory

private[client] trait Logging {
  protected val logger = LoggerFactory.getLogger(this.getClass)
}
