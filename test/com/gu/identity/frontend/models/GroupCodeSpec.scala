package com.gu.identity.frontend.models

import org.scalatestplus.play.PlaySpec

class GroupsSpec extends PlaySpec {

  "Groups" should {

    "return an option of GroupCode object when presented with a valid group code" in {
      GroupCode("GTNF") mustEqual Some(GuardianTeachersNetwork)
    }

    "return None when presented with an invalid group code" in {
      GroupCode("ABC") mustEqual None
    }
  }

}
