package com.gu.identity.frontend.services

import com.gu.identity.model.Consent
import com.gu.identity.service.client.request._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, OptionValues, WordSpec}
import play.api.libs.json.{JsArray, Json}
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
      val displayName = "myDisplayname"
      val firstName = "First"
      val secondName = "Last"
      val receiveGnmMarketing = false
      val receive3rdPartyMarketing = false
      val registrationIp = "123.456.789.012"
      val consents = List(Consent("actor", "firstParty", true))

      val requestBody = RegisterRequestBody(
        email,
        password,
        RegisterRequestBodyPublicFields(displayName),
        RegisterRequestBodyPrivateFields(firstName, secondName, registrationIp),
        consents
      )
      val result: String = handler.handleRequestBody(requestBody)
      val jsonResult = Json.parse(result)

      (jsonResult \ "primaryEmailAddress").validate[String].asOpt.value should equal(email)
      (jsonResult \ "password").validate[String].asOpt.value should equal(password)
      (jsonResult \ "publicFields" \ "displayName").validate[String].asOpt.value should equal(displayName)
      (jsonResult \ "privateFields" \ "firstName").validate[String].asOpt.value should equal(firstName)
      (jsonResult \ "privateFields" \ "secondName").validate[String].asOpt.value should equal(secondName)
      (jsonResult \ "privateFields" \ "registrationIp").validate[String].asOpt.value should equal(registrationIp)
      ((jsonResult \ "consents")(0) \ "hasConsented").validate[Boolean].asOpt.value should equal(consents.head.hasConsented)
    }


    "Encode correct json when using a SendResetPasswordEmailRequest" in {
      val email = "test@guardian.co.uk"

      val requestBody = SendResetPasswordEmailRequestBody(email)

      val result: String = handler.handleRequestBody(requestBody)
      val jsonResult = Json.parse(result)

      (jsonResult \ "email-address").validate[String].asOpt.value should equal(email)
    }
  }
}
