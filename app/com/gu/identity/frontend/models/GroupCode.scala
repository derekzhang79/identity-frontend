package com.gu.identity.frontend.models

import play.api.data.format.Formatter
import play.api.data.{FormError, Forms, FieldMapping}

sealed trait GroupCode extends Product2[GroupCode, String] {
  self =>

  val id: String

  def _1 = self
  def _2 = id
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

  object FormMappings {
    lazy val groupCode: FieldMapping[GroupCode] = Forms.of[GroupCode](GroupCodeFormatter)

    private object GroupCodeFormatter extends Formatter[GroupCode] {
      def bind(key: String, data: Map[String, String]): Either[Seq[FormError], GroupCode] = {
        val value = data.get(key)
          .flatMap(GroupCode.apply)

        value.toRight {
          Seq(FormError(key, "Unknown Group Code"))
        }
      }

      def unbind(key: String, value: GroupCode): Map[String, String] =
        Map(key -> value.id)
    }
  }
}

case object GuardianTeachersNetwork extends GroupCode {
  val id: String = "GTNF"
}
case object GuardianJobs extends GroupCode {
  val id: String = "GRS"
}
