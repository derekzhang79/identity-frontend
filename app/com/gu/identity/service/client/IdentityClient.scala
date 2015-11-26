package com.gu.identity.service.client

import scala.concurrent.{ExecutionContext, Future}


object IdentityClient {

  def authenticateCookies(email: Option[String], password: Option[String])(implicit configuration: IdentityClientConfiguration, ec: ExecutionContext): Future[Either[IdentityClientError, Seq[Cookie]]] =
    AuthenticateCookiesRequest.from(email, password) match {
      case Right(request) => authenticateCookies(request)
      case Left(err) => Future.successful(Left(err))
    }

  def authenticateCookies(request: AuthenticateCookiesRequest)(implicit configuration: IdentityClientConfiguration, ec: ExecutionContext): Future[Either[IdentityClientError, Seq[Cookie]]] =
    makeRequest(request, ApiEndpoints.authenticateEndpoint).map {
      case Left(err) => Left(err)
      case Right(response) => {
        val parsed = configuration.jsonParser.parseAuthenticationCookiesResponse(response.body)

        Right(parsed.cookies.values.map { c =>
          Cookie(c.key, c.value, c.sessionCookie.getOrElse(false), parsed.cookies.expiresAt)
        })
      }
    }


  private def apiKeyHeaders(implicit configuration: IdentityClientConfiguration) =
    Iterable("X-GU-ID-Client-Access-Token" -> s"Bearer ${configuration.apiKey}")

  private def makeRequest(request: ApiRequest, endpoint: Future[String])(implicit configuration: IdentityClientConfiguration, ec: ExecutionContext) =
    endpoint.flatMap { url =>
      request.method match {
        case GET => configuration.httpProvider.get(url, request.parameters, request.headers ++ apiKeyHeaders)
        case POST => configuration.httpProvider.post(url, request.body, request.parameters, request.headers ++ apiKeyHeaders)
        case DELETE => configuration.httpProvider.delete(url, request.body, request.parameters, request.headers ++ apiKeyHeaders)
      }
    }

}
