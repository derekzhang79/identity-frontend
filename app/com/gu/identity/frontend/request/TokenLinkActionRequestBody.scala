package com.gu.identity.frontend.request

import com.gu.identity.frontend.errors.{ResetPasswordActionBadRequestAppException, ForgeryTokenAppException, ResetPasswordInvalidEmailAppException, AppException}
import com.gu.identity.frontend.request.RequestParameters.CSRFTokenRequestParameter
import play.api.data.{FormError, Form}


case class TokenLinkActionRequestBody(
  token: String,
  csrfToken: String)
  extends CSRFTokenRequestParameter


object TokenLinkActionRequestBody {

  lazy val bodyParser = FormRequestBodyParser("TokenLinkActionRequestBody")(_ => tokenFromLinkForm)(handleFormErrors)

  lazy val tokenFromLinkForm = Form(FormMapping.tokenLinkMapping)

  private def handleFormErrors(formError: FormError): AppException = formError match {
    case FormError("email", messages, _) => ResetPasswordInvalidEmailAppException(messages.headOption.getOrElse("Unknown"))
    case FormError("csrfToken", _, _) => ForgeryTokenAppException("Missing csrfToken on request")
    case e => ResetPasswordActionBadRequestAppException(e.message)
  }

  object FormMapping {
    import play.api.data.Forms._

    val tokenLinkMapping =
      mapping(
        "token" -> text,
        "csrfToken" -> text
      )(TokenLinkActionRequestBody.apply)(TokenLinkActionRequestBody.unapply)
  }
}
