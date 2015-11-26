package com.gu.identity.service.client

sealed trait ApiRequest {
  val method: HttpMethod = GET
  val headers: HttpParameters = Nil
  val parameters: HttpParameters = Nil
  val body: Option[String] = None
}


case class AuthenticateCookiesRequest(email: String, password: String) extends ApiRequest {
  override val method = POST
  override val headers = Seq("Content-Type" -> "application/x-www-form-urlencoded")
  override val parameters = Seq("format" -> "cookies")
  override val body = Some(s"email=$email&password=$password")
}


object AuthenticateCookiesRequest {
  private def isValidEmail(email: String): Boolean =
    email.nonEmpty

  private def isValidPassword(password: String): Boolean =
    password.nonEmpty

  def from(email: Option[String], password: Option[String]): Either[BadRequest, AuthenticateCookiesRequest] =
    (email, password) match {
      case (Some(e), Some(p)) if isValidEmail(e) && isValidPassword(p) => Right(AuthenticateCookiesRequest(e, p))
      case _ => Left(BadRequest("Invalid request"))
    }

}
