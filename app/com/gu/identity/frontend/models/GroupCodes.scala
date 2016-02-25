package com.gu.identity.frontend.models

object GroupCodes extends Enumeration {
  type Name = Value
  val GTNF, GRS = Value

  def isOrderType(s: String): Boolean = values.exists(_.toString == s)
}
