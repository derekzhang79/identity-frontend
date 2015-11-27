package com.gu.identity.service.client

import scala.concurrent.{Future, ExecutionContext}


sealed trait HttpMethod
case object GET extends HttpMethod
case object POST extends HttpMethod
case object DELETE extends HttpMethod


case class HttpResponse(body: String, statusCode: Int, statusMessage: String)

object HttpResponse {
  def isBadRequest(response: HttpResponse) =
    response.statusCode >= 400 && response.statusCode < 500
}


trait HttpProvider {
  def get(url: String, urlParameters: HttpParameters = Nil, headers: HttpParameters = Nil)(implicit ec: ExecutionContext): Future[Either[IdentityClientError, HttpResponse]]

  def post(url: String, body: Option[String], urlParameters: HttpParameters = Nil, headers: HttpParameters = Nil)(implicit ec: ExecutionContext): Future[Either[IdentityClientError, HttpResponse]]

  def delete(url: String, body: Option[String] = None, urlParameters: HttpParameters = Nil, headers: HttpParameters = Nil)(implicit ec: ExecutionContext): Future[Either[IdentityClientError, HttpResponse]]
}
