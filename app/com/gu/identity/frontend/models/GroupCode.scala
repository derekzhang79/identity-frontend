package com.gu.identity.frontend.models

sealed trait GroupCode

object GroupCode {
  def apply(group: String): Option[GroupCode] ={
    group match {
      case "GTNF" => Some(GuardianTeachersNetwork)
      case "GRS" => Some(GuardianJobs)
      case _ => None
    }
  }
}

case object GuardianTeachersNetwork extends GroupCode
case object GuardianJobs extends GroupCode
