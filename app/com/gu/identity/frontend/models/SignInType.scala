package com.gu.identity.frontend.models

sealed trait SignInType {
  val id: String
}

object SignInType {
  def apply(signInType: String): Option[SignInType] = {
    signInType match {
      case "existing" => Some(ExistingUser)
      case "guest" => Some(GuestUser)
      case "new" => Some(NewUser)
      case _ => None
    }
  }
}

case object ExistingUser extends SignInType {
  val id: String = "existing"
}

case object GuestUser extends SignInType {
  val id: String = "guest"
}

case object NewUser extends SignInType {
  val id: String = "new"
}
