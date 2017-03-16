package com.gu.identity.frontend.request

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.errors._
import com.gu.identity.frontend.models.{ReturnUrl, ClientID, GroupCode}
import com.gu.identity.frontend.request.RequestParameters._
import play.api.data.Forms._
import play.api.data.{FormError, Mapping, Form}
import play.api.http.HeaderNames
import play.api.mvc.{RequestHeader, Result, BodyParsers, BodyParser}


case class RegisterActionRequestBody private(
                                              firstName: String,
                                              lastName: String,
                                              email: String,
                                              displayName: String,
                                              password: String,
                                              countryCode: Option[String],
                                              localNumber: Option[String],
                                              receiveGnmMarketing: Boolean,
                                              receive3rdPartyMarketing: Boolean,
                                              returnUrl: Option[ReturnUrl],
                                              skipConfirmation: Option[Boolean],
                                              groupCode: Option[GroupCode],
                                              clientId: Option[ClientID],
                                              csrfToken: String,
                                              gaClientId: Option[String])
  extends SignInRequestParameters
  with ReturnUrlRequestParameter
  with SkipConfirmationRequestParameter
  with ClientIdRequestParameter
  with GroupRequestParameter
  with CSRFTokenRequestParameter
  with GaClientIdRequestParameter {

  // activate "rememberMe" on registrations
  val rememberMe = true
}

object RegisterActionRequestBody {

  lazy val bodyParser: BodyParser[RegisterActionRequestBody] =
    FormRequestBodyParser("RegisterActionRequestBody")(registerForm)(handleFormErrors)

  def registerForm(requestHeader: RequestHeader): Form[RegisterActionRequestBody] =
    registerForm(requestHeader.headers.get(HeaderNames.REFERER))

  def registerForm(refererHeader: Option[String]): Form[RegisterActionRequestBody] =
    Form(FormMapping.registerFormMapping(refererHeader))


  private def handleFormErrors(formError: FormError): AppException = formError match {
    case FormError("csrfToken", _, _) => ForgeryTokenAppException("Missing csrfToken on request")
    case FormError("firstName", msg, _) => RegisterActionInvalidFirstNameAppException(msg.headOption.getOrElse("unknown"))
    case FormError("lastName", msg, _) => RegisterActionInvalidLastNameAppException(msg.headOption.getOrElse("unknown"))
    case FormError("email", msg, _) => RegisterActionInvalidEmailAppException(msg.headOption.getOrElse("unknown"))
    case FormError("displayName", msg, _) => RegisterActionInvalidDisplayNameAppException(msg.headOption.getOrElse("unknown"))
    case FormError("password", msg, _) => RegisterActionInvalidPasswordAppException(msg.headOption.getOrElse("unknown"))
    case FormError("groupCode", msg, _) => RegisterActionInvalidGroupAppException(msg.headOption.getOrElse("unknown"))
    case e => RegisterActionBadRequestAppException(s"Unexpected error: ${e.message}")
  }


  object FormMapping {
    import ClientID.FormMapping.clientId
    import GroupCode.FormMappings.groupCode
    import ReturnUrl.FormMapping.returnUrl

    def minMaxLength(min : Int, max: Int): Mapping[String] = text.verifying(
      "error.displayName", name => name.length >= min && name.length <= max
    )

    private val password: Mapping[String] = text.verifying(
      "error.password", name => name.length > 5 && name.length < 73
    )

    def registerFormMapping(refererHeader: Option[String]): Mapping[RegisterActionRequestBody] =
      mapping(
        "firstName" -> minMaxLength(1, 25),
        "lastName" -> minMaxLength(1, 25),
        "email" -> email,
        "displayName" -> minMaxLength(2, 50),
        "password" -> password,
        "countryCode" -> optional(text),
        "localNumber" -> optional(text),
        "receiveGnmMarketing" -> boolean,
        "receive3rdPartyMarketing" -> boolean,
        "returnUrl" -> returnUrl(refererHeader),
        "skipConfirmation" -> optional(boolean),
        "groupCode" -> optional(groupCode),
        "clientId" -> optional(clientId),
        "csrfToken" -> text,
        "gaClientId" -> optional(text)
      )(RegisterActionRequestBody.apply)(RegisterActionRequestBody.unapply)
  }
}
