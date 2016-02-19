package com.gu.identity.frontend.models


/**
 * Defines a Client ID for altering the skin of all pages.
 *
 * Note: Product is manually defined here with `_1` mapping to the object
 *       itself. This is a workaround to ensure the values of the case object
 *       are available in the handlebars template,
 *       see: https://github.com/bizreach/play2-handlebars/issues/11
 */
sealed trait ClientID extends Product2[ClientID, String] {
  self =>

  val id: String

  def _1 = self
  def _2 = id
}

case object GuardianMembersClientID extends ClientID {
  val id = "members"
}

object ClientID {
  def all: Seq[ClientID] = Seq(GuardianMembersClientID)

  def apply(clientId: Option[String]): Option[ClientID] =
    clientId.flatMap { id =>
      all.find(_.id == id)
    }
}
