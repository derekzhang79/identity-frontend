package com.gu.identity.frontend.models

/**
 * Defines a SignInType for determining where to point back links to.
 */

sealed trait SignInType extends Product2[SignInType, String] {
  self =>

  val id: String

  def _1 = self
  def _2 = id
}

case object ClassicSignInType extends SignInType {
  val id = "classic"
}

case object TwoStepSignInType extends SignInType {
  val id = "new"
}


object SignInType {
  def all: Seq[SignInType] = Seq(ClassicSignInType, TwoStepSignInType)

  def apply(SignInType: String): Option[SignInType] =
    all.find(_.id == SignInType)

  def apply(SignInType: Option[String]): Option[SignInType] =
    SignInType.flatMap(apply)
}
