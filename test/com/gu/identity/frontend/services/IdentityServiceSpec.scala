package com.gu.identity.frontend.services

import com.gu.identity.frontend.configuration.ApplicationConfiguration
import com.gu.identity.service.client._
import org.joda.time.DateTime
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec

class IdentityServiceSpec extends PlaySpec with MockitoSugar {

  val mockIdentityClient = mock[IdentityClient]
  val mockIdentityServiceRequestHandler = mock[IdentityServiceRequestHandler]
  val mockConfiguration = mock[ApplicationConfiguration]
  val identityService = new IdentityServiceImpl(mockConfiguration, mockIdentityServiceRequestHandler, mockIdentityClient)

  "The createPlayCookie function" should {

    "Create a secure Play cookie only if the Identity cookie key starts with 'SC_' " in {
      val identityCookie = IdentityCookie("SC_123","",true, new DateTime())
      val playCookie = identityService.createPlayCookie(identityCookie)
      playCookie.secure mustEqual true
    }

    "Not create a secure Play cookie if the Identity cookie key does not start with 'SC_' " in {
      val identityCookie = IdentityCookie("AB_123","",true, new DateTime())
      val playCookie = identityService.createPlayCookie(identityCookie)
      playCookie.secure mustEqual false
    }
  }
}
