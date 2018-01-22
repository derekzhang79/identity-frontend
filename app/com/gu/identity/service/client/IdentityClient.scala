package com.gu.identity.service.client

import com.gu.identity.frontend.authentication.IdentityApiCookie
import com.gu.identity.frontend.models.TrackingData
import com.gu.identity.service.client.request._
import com.gu.identity.service.client.models.User
import scala.concurrent.{ExecutionContext, Future}

class IdentityClient extends Logging {

  def authenticateCookies(email: String, password: String, rememberMe: Boolean, trackingData: TrackingData)(implicit configuration: IdentityClientConfiguration, ec: ExecutionContext): Future[Either[IdentityClientErrors, Seq[IdentityApiCookie]]] =
    AuthenticateCookiesApiRequest(Some(email), Some(password), rememberMe, None, trackingData) match {
      case Right(request) => authenticateCookies(request)
      case Left(err) => Future.successful(Left(Seq(err)))
    }

  def authenticateTokenCookies(token: String, trackingData: TrackingData)(implicit configuration: IdentityClientConfiguration, ec: ExecutionContext): Future[Either[IdentityClientErrors, Seq[IdentityApiCookie]]] =
    AuthenticateCookiesApiRequest(None, None, false, Some(token), trackingData) match {
      case Right(request) => authenticateCookies(request)
      case Left(err) => Future.successful(Left(Seq()))
    }

  private def authenticateCookies(request: ApiRequest)(implicit configuration: IdentityClientConfiguration, ec: ExecutionContext): Future[Either[IdentityClientErrors, Seq[IdentityApiCookie]]] =
    configuration.requestHandler.handleRequest(request).map {
      case Left(error) => Left(error)
      case Right(AuthenticationCookiesResponse(cookies)) =>
        Right(cookies.values.map(IdentityApiCookie(_, cookies.expiresAt)))
      case Right(other) => Left(Seq(ClientGatewayError("Unknown response")))
    }

  def register(request: RegisterApiRequest)(implicit configuration: IdentityClientConfiguration, ec: ExecutionContext): Future[Either[IdentityClientErrors, RegisterResponseUser]] = {
    configuration.requestHandler.handleRequest(request).map {
      case Left(error) => Left(error)
      case Right(RegisterResponse(user)) =>
        Right(user)
      case Right(other) => Left(Seq(ClientGatewayError("Unknown response")))
    }
  }

  def getUser(request: UserApiRequest)(implicit configuration: IdentityClientConfiguration, ec: ExecutionContext): Future[Either[IdentityClientErrors, User]] = {
    configuration.requestHandler.handleRequest(request).map {
      case Left(error) => Left(error)
      case Right(UserResponse(user)) =>
        Right(user)
      case Right(other) => Left(Seq(ClientGatewayError("Unknown response")))
    }
  }

  def assignGroupCode(request: AssignGroupApiRequest)(implicit configuration: IdentityClientConfiguration, ec: ExecutionContext): Future[Either[IdentityClientErrors, AssignGroupResponse]] = {
    configuration.requestHandler.handleRequest(request).map {
      case Left(error) => Left(error)
      case Right(response: AssignGroupResponse) => Right(response)
      case Right(other) => Left(Seq(ClientGatewayError("Unknown response")))
    }
  }

  def sendResetPasswordEmail(request: SendResetPasswordEmailApiRequest)(implicit configuration: IdentityClientConfiguration, ec: ExecutionContext): Future[Either[IdentityClientErrors, SendResetPasswordEmailResponse ]] = {
    configuration.requestHandler.handleRequest(request).map {
      case Left(error) => {
        logger.error("Failed to send reset password email request")
        Left(error)
      }
      case Right(r: SendResetPasswordEmailResponse) => {
        logger.info("Successfully sent reset password email request")
        Right(r)
      }
      case Right(other) => Left(Seq(ClientGatewayError("Unknown response")))
    }
  }

  def ResendConsentToken(request: ResendConsentTokenApiRequest)(implicit configuration: IdentityClientConfiguration, ec: ExecutionContext): Future[Either[IdentityClientErrors, ResendConsentTokenResponse ]] = {
    configuration.requestHandler.handleRequest(request).map {
      case Left(error) =>
        logger.error("Failed to resend consent email link")
        Left(error)

      case Right(r: ResendConsentTokenResponse) => {
        logger.info("Successfully resent consent email")
        Right(r)
      }
      case Right(_) => Left(Seq(ClientGatewayError("Uknown response")))
    }
  }

  def deauthenticate(request: DeauthenticateApiRequest)(implicit configuration: IdentityClientConfiguration, ec: ExecutionContext): Future[Either[IdentityClientErrors, Seq[IdentityApiCookie]]] = {
    configuration.requestHandler.handleRequest(request).map {
      case Left(error) => Left(error)
      case Right(response: DeauthenticationCookiesResponse) => Right({
        val expiresAt = response.cookies.expiresAt
        response.cookies.values.map { c =>
          IdentityApiCookie(name = c.key, value = c.value, isSession = false, expires = expiresAt)
        }
      })
      case Right(other) => Left(Seq(ClientGatewayError("Unknown response")))
    }
  }

  def postConsentToken(token: String)(implicit configuration: IdentityClientConfiguration, ec: ExecutionContext): Future[Either[IdentityClientErrors, ApiResponse]] = {
    configuration.requestHandler.handleRequest(UseConsentTokenRequest(token, configuration))
  }

}
