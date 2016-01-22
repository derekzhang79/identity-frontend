package com.gu.identity.frontend.csrf

import play.api.mvc._
import play.filters.csrf.CSRF.ErrorHandler
import play.filters.csrf.{CSRFAddToken => PlayCSRFAddToken, CSRFCheck => PlayCSRFCheck, CSRF, CSRFConfig}

import scala.concurrent.Future


sealed trait CSRFActions

case class CSRFAddToken(config: CSRFConfig) extends ActionBuilder[Request] with CSRFActions {
  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] =
    block(request)

  override def composeAction[A](action: Action[A]) =
    PlayCSRFAddToken(action, config)

}

case class CSRFCheck(config: CSRFConfig, errorHandler: ErrorHandler = CSRF.DefaultErrorHandler) extends ActionBuilder[Request] with CSRFActions {
  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] =
    block(request)

  override def composeAction[A](action: Action[A]) =
    PlayCSRFCheck(action, errorHandler, config)
}
