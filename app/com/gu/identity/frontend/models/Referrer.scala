package com.gu.identity.frontend.models

/**
 * Defines a Referrer for determining where to point back links to.
 */

sealed trait Referrer extends Product2[Referrer, String] {
  self =>

  val id: String

  def _1 = self
  def _2 = id
}

case object SignInClassicReferrer extends Referrer {
  val id = "signin-classic"
}

case object SignInTwoStepReferrer  extends Referrer {
  val id = "signin-2s"
}


object Referrer {
  def all: Seq[Referrer] = Seq(SignInClassicReferrer, SignInTwoStepReferrer )

  def apply(referrer: String): Option[Referrer] =
    all.find(_.id == referrer)

  def apply(referrer: Option[String]): Option[Referrer] =
    referrer.flatMap(apply)
}
