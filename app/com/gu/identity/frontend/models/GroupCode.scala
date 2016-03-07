package com.gu.identity.frontend.models

sealed trait GroupCode {
  def getCodeValue: String
}

object GroupCode {
  def apply(group: String): Option[GroupCode] ={
    group match {
      case "GTNF" => Some(GuardianTeachersNetwork)
      case "GRS" => Some(GuardianJobs)
      case _ => None
    }
  }

  def apply(group: Option[String]): Option[GroupCode] = apply(group.getOrElse(""))
}

case object GuardianTeachersNetwork extends GroupCode {
  val getCodeValue: String = "GTNF"
}
case object GuardianJobs extends GroupCode {
  val getCodeValue: String = "GRS"
}
