package com.gu.identity.frontend.models.text

import play.api.i18n.Messages

sealed trait ErrorPageText {
  val pageTitle: String
  val title: String
  val description: String
}


/** 404 Not found text */
case class NotFoundErrorPageText private(
    pageTitle: String,
    title: String,
    description: String)
  extends ErrorPageText

object NotFoundErrorPageText {
  def apply()(implicit messages: Messages): NotFoundErrorPageText =
    NotFoundErrorPageText(
      pageTitle = messages("errorpage.404.pageTitle"),
      title = messages("errorpage.404.title"),
      description = messages("errorpage.404.description")
    )
}


/** 5xx Unexpected text */
case class UnexpectedErrorPageText private(
    pageTitle: String,
    title: String,
    description: String)
  extends ErrorPageText

object UnexpectedErrorPageText {
  def apply()(implicit messages: Messages): UnexpectedErrorPageText =
    UnexpectedErrorPageText(
      pageTitle = messages("errorpage.5xx.pageTitle"),
      title = messages("errorpage.5xx.title"),
      description = messages("errorpage.5xx.description")
    )
}
