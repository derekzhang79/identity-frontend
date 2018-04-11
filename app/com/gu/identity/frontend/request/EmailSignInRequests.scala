package com.gu.identity.frontend.request

import com.gu.identity.frontend.errors.SignInInvalidCredentialsAppException
import play.api.data.{Form, Forms, Mapping}
import play.api.data.Forms.{email, optional, nonEmptyText}
import play.api.mvc.BodyParser

case class EmailSignInRequest(email: String, returnUrl: Option[String])

trait EmailSignInRequests {

  protected lazy val emailSigninFormMapping: Mapping[EmailSignInRequest] = Forms.mapping(
    "email" -> email,
    "returnUrl" -> optional(nonEmptyText)
  )(EmailSignInRequest.apply)(EmailSignInRequest.unapply)
  protected lazy val emailSigninForm: Form[EmailSignInRequest] = Form(emailSigninFormMapping)
  protected lazy val emailSigninFormParser: BodyParser[EmailSignInRequest] = FormRequestBodyParser("email_signin")(_ => emailSigninForm)(e => SignInInvalidCredentialsAppException)

}

object EmailSignInRequests extends EmailSignInRequests
