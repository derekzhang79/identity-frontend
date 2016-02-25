package com.gu.identity.frontend.controllers

import com.gu.identity.cookie.IdentityCookieDecoder
import com.gu.identity.frontend.services.IdentityService
import com.gu.identity.service.client.models.{UserGroup, User}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec

class ThirdPartyTsAndCsSpec extends PlaySpec with MockitoSugar{

  val mockIdentityService = mock[IdentityService]
  val mockIdentityCookieDecoder = mock[IdentityCookieDecoder]

  val thirdPartyTsAndCsController = new ThirdPartyTsAndCs(mockIdentityService, mockIdentityCookieDecoder)

  "ThirdPartyTsAndCs" should {

    "return true if user is in group specified" in {
      val groupCode = "ABC"
      val userGroup = UserGroup(groupCode, "Group/ABC")
      val userGroups = List(userGroup)
      val user = User(userGroups = userGroups)

      thirdPartyTsAndCsController.isUserInGroup(user, groupCode) mustEqual true
    }

    "return false if user is not in the group specified" in {
      val groupCode = "123"
      val userGroup = UserGroup("ABC", "Group/ABC")
      val userGroups = List(userGroup)
      val user = User(userGroups = userGroups)

      thirdPartyTsAndCsController.isUserInGroup(user, groupCode) mustEqual false
    }
  }

}
