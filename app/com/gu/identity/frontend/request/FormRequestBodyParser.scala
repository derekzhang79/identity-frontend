package com.gu.identity.frontend.request

import com.gu.identity.frontend.errors.{AppException, SeqAppExceptions}
import play.api.data.{FormError, Form}
import play.api.mvc.{Result, BodyParsers, RequestHeader, BodyParser}

/**
 * Wrapper on Play's Form Body Parser to provide a simpler interface for
 * transforming form parse errors.
 */
object FormRequestBodyParser {

  def apply[T](debugName: String)(form: RequestHeader => Form[T])(errorHandler: FormError => AppException): BodyParser[T] =
    BodyParser(s"FormRequestBodyParser:$debugName") { requestHeader =>
      BodyParsers.parse.form(
        form(requestHeader),
        onErrors = onParserErrors(errorHandler)
      ).apply(requestHeader)
    }


  // Unfortunately need to throw errors here as play's parser syntax doesn't
  // allow returning a typed error, only a result
  private def onParserErrors(errorHandler: FormError => AppException)(form: Form[_]): Result =
    throw {
      if (form.errors.size == 1) errorHandler(form.errors.head)
      else SeqAppExceptions {
        form.errors.map(errorHandler)
      }
    }

}
