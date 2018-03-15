package com.gu.identity.frontend.models

sealed abstract class SignInType(val id: String)

object SignInType {
  private val all: Seq[SignInType] = Seq(ExistingUser, GuestUser, NewUser)
  def apply(signInType: String) = all.find(_.id == signInType)
}

case object ExistingUser extends SignInType("current")
case object GuestUser extends SignInType("guest")
case object NewUser extends SignInType("new")
