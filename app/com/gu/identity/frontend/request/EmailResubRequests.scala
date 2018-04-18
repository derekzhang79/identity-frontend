package com.gu.identity.frontend.request

import com.gu.identity.frontend.errors.SignInInvalidCredentialsAppException
import play.api.data.{Form, Forms, Mapping}
import play.api.data.Forms.{email, optional, nonEmptyText}
import play.api.mvc.BodyParser

case class EmailResubscribeRequest(email: String, returnUrl: Option[String])

trait EmailResubRequests {

  protected lazy val emailResubFormMapping: Mapping[EmailResubscribeRequest] = Forms.mapping(
    "email" -> email,
    "returnUrl" -> optional(nonEmptyText)
  )(EmailResubscribeRequest.apply)(EmailResubscribeRequest.unapply)
  protected lazy val emailResubForm: Form[EmailResubscribeRequest] = Form(emailResubFormMapping)
  protected lazy val emailResubFormParser: BodyParser[EmailResubscribeRequest] = FormRequestBodyParser("email_signin")(_ => emailResubForm)(e => SignInInvalidCredentialsAppException)

}

object EmailResubRequests extends EmailResubRequests
