package com.gu.identity.frontend.views.models

import play.api.i18n.Messages

trait ViewModel {
  def toMap(implicit messages: Messages): Map[String, Any]
}
