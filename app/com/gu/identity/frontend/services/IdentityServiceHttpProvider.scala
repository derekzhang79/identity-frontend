package com.gu.identity.frontend.services

import javax.inject.{Inject, Singleton}

import com.gu.identity.service.client._
import play.api.libs.ws.WSClient

import scala.concurrent.{Future, ExecutionContext}
import scala.util.control.NonFatal


@Singleton
class IdentityServiceHttpProvider @Inject() (ws: WSClient) extends HttpProvider {
  private def request(method: String, url: String, body: Option[String], urlParameters: HttpParameters, headers: HttpParameters)(implicit ec: ExecutionContext) = {
    ws.url(url)
      .withHeaders(headers.toSeq: _*)
      .withQueryString(urlParameters.toSeq: _*)
      .withRequestTimeout(10000)
      .withBody(body.getOrElse(""))
      .execute(method).map { response =>
      Right(HttpResponse(response.body, response.status, response.statusText))
    }.recoverWith {
      // TODO: recover 4xx errors as Future.successful(Left) and 5xx errors as Future.failed(GatewayError)
      case NonFatal(err) => Future.failed(GatewayError(err, err.getMessage))
    }
  }

  override def get(url: String, urlParameters: HttpParameters, headers: HttpParameters)(implicit ec: ExecutionContext): Future[Either[IdentityClientError, HttpResponse]] =
    request("GET", url, None, urlParameters, headers)

  override def post(url: String, body: Option[String], urlParameters: HttpParameters, headers: HttpParameters)(implicit ec: ExecutionContext): Future[Either[IdentityClientError, HttpResponse]] =
    request("POST", url, body, urlParameters, headers)

  override def delete(url: String, body: Option[String], urlParameters: HttpParameters, headers: HttpParameters)(implicit ec: ExecutionContext): Future[Either[IdentityClientError, HttpResponse]] =
    request("DELETE", url, body, urlParameters, headers)
}
