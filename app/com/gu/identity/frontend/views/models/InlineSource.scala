package com.gu.identity.frontend.views.models

import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils
import play.api.i18n.MessagesApi

/**
 * Inline Javascript or CSS source file. Required an accompanied sha256 hash
 * for Content Security Policy.
 */
case class InlineSource(source: String, sha256: String) extends ViewModel {
  override def toMap(implicit messages: MessagesApi) = Map("source" -> source, "sha256" -> sha256)
}

object InlineSource {
  def apply(source: String): InlineSource =
    InlineSource(source, sha256(source).getOrElse(""))

  private def sha256(in: String) =
    Option(Base64.encodeBase64String(DigestUtils.sha256(in)))
}


