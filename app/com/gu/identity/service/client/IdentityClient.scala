package com.gu.identity.service.client

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal


object IdentityClient extends Logging {

  def authenticateCookies(email: Option[String], password: Option[String])(implicit configuration: IdentityClientConfiguration, ec: ExecutionContext): Future[Either[IdentityClientErrors, Seq[Cookie]]] =
    AuthenticateCookiesRequest.from(email, password) match {
      case Right(request) => authenticateCookies(request)
      case Left(err) => Future.successful(Left(Seq(err)))
    }

  def authenticateCookies(request: AuthenticateCookiesRequest)(implicit configuration: IdentityClientConfiguration, ec: ExecutionContext): Future[Either[IdentityClientErrors, Seq[Cookie]]] =
    makeRequest(request, ApiEndpoints.authenticateEndpoint).map {
      case Left(err) => Left(err)
      case Right(response) => {
        configuration.jsonParser.extractAuthenticationCookiesResponse(response.body)
          .map { parsed =>
            Right(parsed.cookies.values.map { c =>
              Cookie(c.key, c.value, c.sessionCookie.getOrElse(false), parsed.cookies.expiresAt)
            })
          }
          .recover {
            case err => Left {
              Seq(GatewayError("Unexpected API response error", Some(err.getMessage), cause = Some(err)))
            }
          }
          .get
      }
    }


  private def apiKeyHeaders(implicit configuration: IdentityClientConfiguration) =
    Iterable("X-GU-ID-Client-Access-Token" -> s"Bearer ${configuration.apiKey}")


  private def makeRequest(request: ApiRequest, endpoint: Future[String])(implicit configuration: IdentityClientConfiguration, ec: ExecutionContext): Future[Either[IdentityClientErrors, HttpResponse]] =
    endpoint.flatMap { url =>
      request.method match {
        case GET => configuration.httpProvider.get(url, request.parameters, request.headers ++ apiKeyHeaders)
        case POST => configuration.httpProvider.post(url, request.body, request.parameters, request.headers ++ apiKeyHeaders)
        case DELETE => configuration.httpProvider.delete(url, request.body, request.parameters, request.headers ++ apiKeyHeaders)
      }

    }.map {
      case Left(e) => Left(Seq(e))
      case Right(response) if response.statusCode >= 400 && response.statusCode < 600 => Left(parseErrorResponse(response))
      case Right(response) => Right(response)
    }


  private def parseErrorResponse(response: HttpResponse)(implicit configuration: IdentityClientConfiguration): IdentityClientErrors = {
    configuration.jsonParser.extractErrorResponse(response.body)
      .map { errorResponse =>
        if (HttpResponse.isBadRequest(response)) {
          errorResponse.errors.map(e => BadRequest(e.message, e.description, e.context))
        } else {
          errorResponse.errors.map(e => GatewayError(e.message, e.description, e.context))
        }
      }
      .recover {
        case NonFatal(err) => Seq {
          logger.warn(s"Could not parse unknown ${response.statusCode} error response from. Body: ${response.body}", err)

          if (HttpResponse.isBadRequest(response)) {
            BadRequest(s"Bad request: ${response.statusCode} ${response.statusMessage}")

          } else {
            GatewayError(s"Unknown error: ${response.statusCode} ${response.statusMessage}")
          }
        }
      }
      .get
  }

}
