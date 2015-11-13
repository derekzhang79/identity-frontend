package com.gu.identity.frontend.views.models

trait ViewModel {
  def toMap: Map[String, Any]
}
