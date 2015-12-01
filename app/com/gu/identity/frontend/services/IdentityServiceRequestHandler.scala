package com.gu.identity.frontend.services

import javax.inject.Inject

import com.gu.identity.service.client._
import play.api.libs.json.Json
import play.api.libs.json.Reads.jodaDateReads
import play.api.libs.ws.{WSResponse, WSClient}

import scala.concurrent.Future
import scala.util.control.NonFatal

import play.api.libs.concurrent.Execution.Implicits.defaultContext

class IdentityServiceRequestHandler @Inject() (ws: WSClient) extends IdentityClientRequestHandler {

  implicit val dateReads = jodaDateReads("yyyy-MM-dd'T'HH:mm:ssZ")

  implicit val apiErrorResponseErrorReads = Json.format[ApiErrorResponseError]
  implicit val apiErrorResponseReads = Json.format[ApiErrorResponse]

  implicit val responseCookieReads = Json.format[AuthenticationCookiesResponseCookie]
  implicit val responseCookiesListReads = Json.format[AuthenticationCookiesResponseCookieList]
  implicit val responseReads = Json.format[AuthenticationCookiesResponse]


  def handleResponse(request: ApiRequest)(response: WSResponse): Either[IdentityClientErrors, ApiResponse] = request match {
    case r if response.status >= 400 && response.status < 600 => Left {
      response.json.asOpt[ApiErrorResponse]
        .map(_.errors.map {
          case e if response.status < 500 => BadRequest(e.message, e.description, e.context)
          case e => GatewayError(e.message, e.description, e.context)
        })
        .getOrElse {
          Seq(
            // TODO log response
            if (response.status < 500) {
              BadRequest(s"Bad request: ${response.status} ${response.status}")

            } else {
              GatewayError(s"Unknown error: ${response.status} ${response.status}")
            }
          )
        }
    }

    case r: AuthenticateCookiesRequest =>
      response.json.asOpt[AuthenticationCookiesResponse]
        .map(Right.apply)
        .getOrElse {
          // TODO log response
          Left(Seq(GatewayError("Unexpected response from server")))
        }

    case _ => Left(Seq(GatewayError("Unsupported request")))
  }

  def handleRequest(request: ApiRequest): Future[Either[IdentityClientErrors, ApiResponse]] =
    ws.url(request.url)
      .withHeaders(request.headers.toSeq: _*)
      .withQueryString(request.parameters.toSeq: _*)
      .withRequestTimeout(10000)
      .withBody(request.body.getOrElse(""))
      .execute(request.method.toString)
        .map(handleResponse(request))
        .recoverWith {
          case NonFatal(err) => Future.failed {
            GatewayError(
              "Request Error",
              Some(s"Error executing ${request.method} request to: ${request.url} - ${err.getMessage}"),
              cause = Some(err)
            )
          }
        }

}
