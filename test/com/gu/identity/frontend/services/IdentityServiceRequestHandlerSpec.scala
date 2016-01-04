package com.gu.identity.frontend.services

import com.gu.identity.service.client.{AuthenticateCookiesRequestBody, AuthenticateCookiesRequest}
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.ws.WSClient

class IdentityServiceRequestHandlerSpec extends WordSpec with Matchers with MockitoSugar {

  val mockWSClient = mock[WSClient]

  val handler = new IdentityServiceRequestHandler(mockWSClient)

  "When handling the request body" should {

    "Encode params correctly when using an Authenticate Cookies request." in {
      val email = "test@guardian.co.uk"
      val password = "some%thing"

      val requestBody = AuthenticateCookiesRequestBody(email, password)
      val result = handler.handleRequestBody(requestBody)

      result should equal ("email=test%40guardian.co.uk&password=some%25thing")
    }
  }

}
