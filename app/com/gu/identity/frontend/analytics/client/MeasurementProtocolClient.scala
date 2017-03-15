package com.gu.identity.frontend.analytics.client

import com.gu.identity.frontend.logging.Logging
import play.api.libs.ws.WSClient


class MeasurementProtocolClient(ws: WSClient) extends Logging {
  def sendSuccessfulSigninEvent(signinEventRequest: SigninEventRequest) = makeRequest(signinEventRequest)

  def sendSuccessfulRegisterEvent(registerEventRequest: RegisterEventRequest) = makeRequest(registerEventRequest)

  private def makeRequest(request: MeasurementProtocolRequest) =
    request.body.foreach { body =>
      ws.url(request.url)
        .withRequestTimeout(2000)
        .post(body)
    }

}
