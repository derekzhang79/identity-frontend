package com.gu.identity.frontend.errors

import com.gu.identity.frontend.controllers.NoCache
import com.gu.identity.frontend.models.UrlBuilder
import com.gu.identity.frontend.request.RequestParameters.CoreSessionParameters
import com.gu.identity.frontend.utils.ErrorRecoveryActionBuilder
import play.api.libs.json._
import play.api.mvc._
import play.api.mvc.Results.{SeeOther, BadRequest}

import scala.concurrent.Future
import scala.util.control.NonFatal


/**
 * Recover from application errors. Other errors will be caught by the global
 * error handler, eg: 500s.
 */
case class RedirectOnError(route: String) extends ErrorRecoveryActionBuilder {

  def recoverErrors[A](request: Request[A]) = PartialFunction[Throwable, Future[Result]] {
    case ex: AppException => redirectResultFromAppException(request, ex)
    case NonFatal(ex) =>
      redirectResultFromAppException(request, UnexpectedAppException(s"Unexpected error: ${ex.getMessage}", Some(ex)))
  }


  def redirectResultFromAppException[A](request: Request[A], error: AppException): Future[Result] = {
    val url = request.body match {
      case CoreSessionParameters(returnUrl, skipConfirmation, clientId, groupCode) =>
        UrlBuilder(route, returnUrl, skipConfirmation, clientId, groupCode, error)

      case _ =>
        UrlBuilder(route, error)
    }

    Future.successful {
      NoCache(SeeOther(url))
    }
  }

}

case class ResultOnError(route: String) extends ErrorRecoveryActionBuilder {

  def recoverErrors[A](request: Request[A]) = PartialFunction[Throwable, Future[Result]] {
    case ex: AppException => resultFromAppException(request, ex)
    case NonFatal(ex) =>
      resultFromAppException(request, UnexpectedAppException(s"Unexpected error: ${ex.getMessage}", Some(ex)))
  }


  def resultFromAppException[A](request: Request[A], error: AppException): Future[Result] = {
    val url = request.body match {
      case CoreSessionParameters(returnUrl, skipConfirmation, clientId, groupCode) =>
        UrlBuilder(route, returnUrl, skipConfirmation, clientId, groupCode, error)

      case _ =>
        UrlBuilder(route, error)
    }

    Future.successful {
      NoCache(BadRequest(JsObject(Seq("url" -> JsString(url)))))
    }
  }

}
