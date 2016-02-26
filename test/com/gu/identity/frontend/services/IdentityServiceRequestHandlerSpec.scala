package com.gu.identity.frontend.services

import com.gu.identity.service.client.request._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{OptionValues, Matchers, WordSpec}
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

class IdentityServiceRequestHandlerSpec extends WordSpec with Matchers with MockitoSugar with OptionValues {

  val mockWSClient = mock[WSClient]

  val handler = new IdentityServiceRequestHandler(mockWSClient)

  "When handling the request body" should {

    "Encode params correctly when using an Authenticate Cookies request." in {
      val email = "test@guardian.co.uk"
      val password = "some%thing"

      val requestBody = AuthenticateCookiesApiRequestBody(email, password)
      val result = handler.handleRequestBody(requestBody)

      result should equal ("email=test%40guardian.co.uk&password=some%25thing")
    }

    "Encode correct json when using a Register Request." in {
      val email = "test@guardian.co.uk"
      val password = "some%thing"
      val username = "myUsername"
      val firstName = "First"
      val secondName = "Last"
      val receiveGnmMarketing = false
      val receive3rdPartyMarketing = false
      val registrationIp = "123.456.789.012"

      val requestBody = RegisterRequestBody(
        email,
        password,
        RegisterRequestBodyPublicFields(username),
        RegisterRequestBodyPrivateFields(firstName, secondName, registrationIp),
        RegisterRequestBodyStatusFields(receiveGnmMarketing, receive3rdPartyMarketing)
      )
      val result: String = handler.handleRequestBody(requestBody)
      val jsonResult = Json.parse(result)

      (jsonResult \ "primaryEmailAddress").validate[String].asOpt.value should equal(email)
      (jsonResult \ "password").validate[String].asOpt.value should equal(password)
      (jsonResult \ "publicFields" \ "username").validate[String].asOpt.value should equal(username)
      (jsonResult \ "privateFields" \ "firstName").validate[String].asOpt.value should equal(firstName)
      (jsonResult \ "privateFields" \ "secondName").validate[String].asOpt.value should equal(secondName)
      (jsonResult \ "privateFields" \ "registrationIp").validate[String].asOpt.value should equal(registrationIp)
      (jsonResult \ "statusFields" \ "receiveGnmMarketing").validate[Boolean].asOpt.value should equal(receiveGnmMarketing)
      (jsonResult \ "statusFields" \ "receive3rdPartyMarketing").validate[Boolean].asOpt.value should equal(receive3rdPartyMarketing)
    }
  }
}
