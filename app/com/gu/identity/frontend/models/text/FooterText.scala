package com.gu.identity.frontend.models.text

import play.api.i18n.Messages

case class FooterText private(
    help: String,
    terms: String,
    contact: String,
    privacy: String,
    techFeedback: String,
    cookies: String,
    copyright: String)

object FooterText {
  def apply()(implicit messages: Messages): FooterText =
    FooterText(
      help = messages("footer.help"),
      terms = messages("footer.terms"),
      contact = messages("footer.contact"),
      privacy = messages("footer.privacy"),
      techFeedback = messages("footer.techfeedback"),
      cookies = messages("footer.cookies"),
      copyright = messages("footer.copyright")
    )
}
