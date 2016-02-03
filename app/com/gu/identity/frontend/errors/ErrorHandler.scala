package com.gu.identity.frontend.errors

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.logging.Logging
import com.gu.identity.frontend.views.ViewRenderer
import play.api.http.DefaultHttpErrorHandler
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{RequestHeader, Result}
import play.api.mvc.Results.{BadRequest, Forbidden, InternalServerError, NotFound, Status}
import play.api.routing.Router
import play.api.{Environment, UsefulException}
import play.core.SourceMapper
import play.twirl.api.Html

import scala.concurrent.Future


class ErrorHandler(
    configuration: Configuration,
    val messagesApi: MessagesApi,
    environment: Environment,
    sourceMapper: Option[SourceMapper],
    router: => Option[Router])

  extends DefaultHttpErrorHandler(
    environment,
    configuration.underlying,
    sourceMapper,
    router)

  with Logging with I18nSupport {


  override def onNotFound(request: RequestHeader, message: String): Future[Result] =
    renderErrorPage(NotFoundError(message), NotFound.apply)

  override def onBadRequest(request: RequestHeader, message: String): Future[Result] =
    renderErrorPage(BadRequestError(message), BadRequest.apply)

  override def onForbidden(request: RequestHeader, message: String): Future[Result] =
    renderErrorPage(ForbiddenError(message), Forbidden.apply)

  override def onOtherClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] =
    renderErrorPage(BadRequestError(message, statusCode), Status(statusCode).apply)

  override def onProdServerError(request: RequestHeader, ex: UsefulException): Future[Result] =
    renderErrorPage(UnexpectedError(ex), InternalServerError.apply)


  protected def renderErrorPage(error: HttpError, resultGenerator: Html => Result): Future[Result] =
    Future.successful {
      ViewRenderer.renderErrorPage(configuration, error, resultGenerator)
    }
}
