package com.gu.identity.frontend.services

import com.gu.identity.service.client.{RegisterRequestBodyPrivateFields, RegisterRequestBodyPublicFields, RegisterRequestBody, AuthenticateCookiesRequestBody}
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

    "Encode correct json when using a Register Request." in {
      val email = "test@guardian.co.uk"
      val password = "some%thing"
      val username = "myUsername"
      val firstName = "First"
      val lastName = "Last"
      val receiveGnmMarketing = false
      val receive3rdPartyMarketing = false
      val registrationIp = "123.456.789.012"

      val requestBody = RegisterRequestBody(
        email,
        password,
        RegisterRequestBodyPublicFields(username),
        RegisterRequestBodyPrivateFields(firstName, lastName, receiveGnmMarketing, receive3rdPartyMarketing, registrationIp)
      )
      val result = handler.handleRequestBody(requestBody)
      val expectedResult = s"""{"primaryEmailAddress":"$email","password":"$password","publicFields":{"username":"$username"},"privateFields":{"firstName":"$firstName","lastName":"$lastName","receiveGnmMarketing":$receiveGnmMarketing,"receive3rdPartyMarketing":$receive3rdPartyMarketing,"registrationIp":"$registrationIp"}}"""

      result should equal (expectedResult)
    }
  }
}
