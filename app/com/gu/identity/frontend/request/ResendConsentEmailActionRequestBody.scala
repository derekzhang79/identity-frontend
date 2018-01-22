package com.gu.identity.frontend.request

import com.gu.identity.frontend.errors._
import com.gu.identity.frontend.request.RequestParameters.CSRFTokenRequestParameter
import play.api.data.{Form, FormError}


case class ResendConsentTokenActionRequestBody(
  token: String,
  csrfToken: String
)
  extends CSRFTokenRequestParameter


object ResendConsentTokenActionRequestBody {

  lazy val bodyParser = FormRequestBodyParser("ResendConsentTokenActionRequestBody")(_ => resendConsentTokenForm)(handleFormErrors)

  lazy val resendConsentTokenForm = Form(FormMapping.resetConsentMapping)

  private def handleFormErrors(formError: FormError): AppException = formError match {
    case FormError("token", messages, _) => ResendConsentTokenBadTokenExeption(messages.headOption.getOrElse("Unknown"))
    case FormError("csrfToken", _, _) => ForgeryTokenAppException("Missing csrfToken on request")
    case e => ResendConsentTokenBadAppExeption(e.message)
  }

  object FormMapping {
    import play.api.data.Forms._

    val resetConsentMapping =
      mapping(
        "token" -> text,
        "csrfToken" -> text
      )(ResendConsentTokenActionRequestBody.apply)(ResendConsentTokenActionRequestBody.unapply)
  }
}
