package com.gu.identity.frontend.views.models

import play.api.i18n.MessagesApi

trait ViewModel {
  def toMap(implicit messages: MessagesApi): Map[String, Any]
}
