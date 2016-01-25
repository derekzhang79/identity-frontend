package com.gu.identity.frontend.csrf

import com.gu.identity.frontend.controllers.NoCache
import play.api.mvc._
import play.filters.csrf.CSRF.ErrorHandler
import play.filters.csrf.{CSRFAddToken => PlayCSRFAddToken, CSRFCheck => PlayCSRFCheck, CSRF}

import scala.concurrent.Future


sealed trait CSRFActions

case class CSRFAddToken(config: CSRFConfig) extends ActionBuilder[Request] with CSRFActions {
  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] =
    NoCache(block(request))

  override def composeAction[A](action: Action[A]) =
    if (config.enabled)
      PlayCSRFAddToken(action, config.underlying)

    else
      action

}

case class CSRFCheck(config: CSRFConfig, errorHandler: CSRFErrorHandler = defaultErrorHandler) extends ActionBuilder[Request] with CSRFActions {
  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] =
    NoCache(block(request))

  override def composeAction[A](action: Action[A]) =
    if (config.enabled)
      PlayCSRFCheck(action, playErrorHandler, config.underlying)

    else
      action

  lazy val playErrorHandler = new ErrorHandler {
    def handle(req: RequestHeader, msg: String): Future[Result] = errorHandler(req, msg)
  }
}
