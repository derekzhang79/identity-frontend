package com.gu.identity.frontend.models

import org.scalatestplus.play.PlaySpec

class SignInTypeSpec extends PlaySpec {

  "SignInType" should {

    "return an option of SignInType object when presented with a valid sign in type" in {
      SignInType("guest") mustEqual Some(GuestUser)
    }

    "return None when presented with an invalid sign in type" in {
      SignInType("ABC") mustEqual None
    }
  }

}
