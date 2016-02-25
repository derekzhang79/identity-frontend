package com.gu.identity.frontend.models

import org.scalatestplus.play.PlaySpec

class GroupsSpec extends PlaySpec {

  "Groups" should {

    "return true when presented with a valid group code" in {
      GroupCodes.isOrderType("GTNF") mustEqual true
    }

    "return false when presented with an invalid group code" in {
      GroupCodes.isOrderType("ABC") mustEqual false
    }
  }

}
