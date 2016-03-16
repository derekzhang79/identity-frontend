package com.gu.identity.frontend.services

import com.gu.identity.frontend.authentication.CookieService
import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.errors._
import com.gu.identity.frontend.models.{ClientIp, TrackingData}
import com.gu.identity.service.client.models.User
import com.gu.identity.frontend.request.{ResetPasswordActionRequestBody, RegisterActionRequestBody}
import com.gu.identity.frontend.request.RequestParameters.SignInRequestParameters
import com.gu.identity.service.client._
import com.gu.identity.service.client.request._
import org.joda.time.{DateTime, Seconds}
import play.api.mvc.{Cookie => PlayCookie}

import com.gu.identity.frontend.logging.Logging

import scala.concurrent.{ExecutionContext, Future}


/**
 * Adapter for the identity service client.
 */
trait IdentityService {
  type PlayCookies = Seq[PlayCookie]

  def authenticate(signInRequest: SignInRequestParameters, trackingData: TrackingData)(implicit ec: ExecutionContext): Future[Either[ServiceExceptions, PlayCookies]]
  def deauthenticate(cookie: PlayCookie, trackingData: TrackingData)(implicit ec: ExecutionContext): Future[Either[ServiceExceptions, PlayCookies]]
  def registerThenSignIn(request: RegisterActionRequestBody, clientIp: ClientIp, trackingData: TrackingData)(implicit ec: ExecutionContext): Future[Either[ServiceExceptions, PlayCookies]]
  def register(request: RegisterActionRequestBody, clientIp: ClientIp, trackingData: TrackingData)(implicit ec: ExecutionContext): Future[Either[ServiceExceptions, RegisterResponseUser]]
  def sendResetPasswordEmail(data: ResetPasswordActionRequestBody, clientIp: ClientIp)(implicit ec: ExecutionContext): Future[Either[ServiceExceptions, SendResetPasswordEmailResponse ]]
  def getUser(cookie: PlayCookie)(implicit ec: ExecutionContext): Future[Either[ServiceExceptions, User]]
  def assignGroupCode(group: String, cookie: PlayCookie)(implicit ec: ExecutionContext): Future[Either[ServiceExceptions, AssignGroupResponse]]
}


class IdentityServiceImpl(config: Configuration, adapter: IdentityServiceRequestHandler, client: IdentityClient) extends IdentityService with Logging {

  implicit val clientConfiguration = IdentityClientConfiguration(config.identityApiHost, config.identityApiKey, adapter)

  override def authenticate(signInRequest: SignInRequestParameters, trackingData: TrackingData)(implicit ec: ExecutionContext) = {
    client.authenticateCookies(signInRequest.email, signInRequest.password, signInRequest.rememberMe, trackingData).map {
      case Left(errors) =>
        Left(errors.map(SignInServiceAppException.apply))

      case Right(cookies) => Right(CookieService.signInCookies(cookies, signInRequest.rememberMe)(config))
    }
  }

  override def deauthenticate(cookie: PlayCookie, trackingData: TrackingData)(implicit ec: ExecutionContext) = {
    val apiRequest = DeauthenticateApiRequest(cookie, trackingData)
    client.deauthenticate(apiRequest).map {
      case Left(errors) => Left {
        errors.map {
          case e: BadRequest => UnexpectedAppException(e.message)
          case e: GatewayError => UnexpectedAppException(e.message)
        }
      }
      case Right(cookies) => Right(CookieService.signOutCookies(cookies)(config))
    }
  }

  override def register(request: RegisterActionRequestBody, clientIp: ClientIp, trackingData: TrackingData)(implicit ec: ExecutionContext): Future[Either[ServiceExceptions, RegisterResponseUser]] = {
    val apiRequest = RegisterApiRequest(request, clientIp, trackingData)
    client.register(apiRequest).map {
      case Left(errors) =>
        Left(errors.map(RegisterServiceAppException.apply))

      case Right(user) => Right(user)
    }
  }

  override def registerThenSignIn(request: RegisterActionRequestBody,
                                  clientIp: ClientIp,
                                  trackingData: TrackingData
                                 )(implicit ec: ExecutionContext): Future[Either[ServiceExceptions, PlayCookies]] = {
    register(request, clientIp, trackingData).flatMap{
      case Left(errors) => Future.successful(Left(errors))
      case Right(user) => {
        authenticate(request, trackingData).map {
          case Left(signInErrors) => {
            logger.error(s"User could not be logged in after registering: $signInErrors")
            signInErrors.foreach { err =>
              logger.error(s"Sign in error after registering: ${err.getMessage}", err)
            }
            Right(Seq.empty)
          }
          case Right(cookies) => Right(cookies)
        }
      }
    }
  }

  override def sendResetPasswordEmail(resetPasswordData: ResetPasswordActionRequestBody, clientIp: ClientIp)(implicit ec: ExecutionContext): Future[Either[ServiceExceptions, SendResetPasswordEmailResponse ]] = {
    val apiRequest = SendResetPasswordEmailApiRequest(resetPasswordData, clientIp)
    client.sendResetPasswordEmail(apiRequest).map {
      case Left(errors) =>
        Left(errors.map(ResetPasswordAppException.apply))

      case Right(okResponse) => Right(okResponse)
    }
  }

  override def getUser(cookie: PlayCookie)(implicit ec: ExecutionContext): Future[Either[ServiceExceptions, User]] = {
    val apiRequest = UserApiRequest(cookie)
    client.getUser(apiRequest).map {
      case Left(errors) => Left {
        errors.map {
          case e: BadRequest => UnexpectedAppException(e.message)
          case e: GatewayError => UnexpectedAppException(e.message)
        }
      }
      case Right(user) => Right(user)
    }
  }

  override def assignGroupCode(group: String, cookie: PlayCookie)(implicit ec: ExecutionContext): Future[Either[ServiceExceptions, AssignGroupResponse]] = {
    val apiRequest = AssignGroupApiRequest(group, cookie)
    client.assignGroupCode(apiRequest).map {
      case Left(errors) => Left {
        errors.map {
          case e: BadRequest => UnexpectedAppException(e.message)
          case e: GatewayError => UnexpectedAppException(e.message)
        }
      }
      case Right(response) => Right(response)
    }
  }
}
