package com.gu.identity.frontend.configuration

import com.gu.identity.frontend.logging.Logging
import play.api.{UsefulException, Environment}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Result, RequestHeader, Results}
import play.api.http.DefaultHttpErrorHandler
import play.api.routing.Router
import play.core.SourceMapper
import com.gu.identity.frontend.views.ViewRenderer.renderErrorPage

import scala.concurrent.Future


class ErrorHandler(
    configuration: Configuration,
    val messagesApi: MessagesApi,
    environment: Environment,
    sourceMapper: Option[SourceMapper],
    router: => Option[Router])
  extends DefaultHttpErrorHandler(
    environment,
    configuration.appConfiguration,
    sourceMapper,
    router
  )
  with Logging with I18nSupport {

  override def onNotFound(request: RequestHeader, message: String): Future[Result] =
    Future.successful {
      renderErrorPage(configuration, Results.NotFound.apply)
    }

  // TODO properly display 5xx error
  override def onProdServerError(request: RequestHeader, exception: UsefulException): Future[Result] =
    Future.successful {
      renderErrorPage(configuration, Results.InternalServerError.apply)
    }
}
