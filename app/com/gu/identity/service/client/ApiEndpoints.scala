package com.gu.identity.service.client

import scala.concurrent.Future

/**
 * Retrieve endpoints for the Identity API.
 *
 * All endpoints should be resolved as futures to allow for hypermedia resolution of endpoints in the future
 */
object ApiEndpoints {

  def authenticateEndpoint(implicit configuration: IdentityClientConfiguration): Future[String] =
    Future.successful(s"https://${configuration.host}/auth")


}
