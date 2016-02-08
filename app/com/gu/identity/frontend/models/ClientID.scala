package com.gu.identity.frontend.models


sealed trait ClientID {
  val id: String
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
