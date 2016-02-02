package com.gu.identity.frontend.errors

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.test.SimpleAppPerSuite
import org.scalatest.BeforeAndAfter
import play.api.ApplicationLoader.Context
import play.api.i18n.{MessagesApi, I18nComponents}
import play.api.mvc.Result
import play.api.routing.Router
import play.api._
import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.play.PlaySpec
import play.core.SourceMapper
import play.twirl.api.Html

import scala.concurrent.Future


class ErrorHandlerSpec extends PlaySpec with SimpleAppPerSuite with BeforeAndAfter {

  override lazy val app: Application = FakeApplicationWithErrorHandler()

  // crude state holder for error passed to error handler
  var lastError: Option[HttpError] = None

  before {
    lastError = None
  }

  "Error handler" must {

    "display 404 not found error" in {
      val Some(resp) = route(FakeRequest(GET, "/iDoNotExist"))

      status(resp) must equal(404)
      contentAsString(resp) must include("mocked error page")

      lastError.value mustBe a [NotFoundError]
    }

  }


  class MockedErrorHandler(
      configuration: Configuration,
      messagesApi: MessagesApi,
      environment: Environment,
      sourceMapper: Option[SourceMapper],
      router: => Option[Router])
    extends ErrorHandler(configuration, messagesApi, environment, sourceMapper, router) {

    override def renderErrorPage(error: HttpError, resultGenerator: Html => Result) = {
      lastError = Some(error)

      Future.successful(resultGenerator(Html("mocked error page")))
    }
  }


  object FakeApplicationWithErrorHandler {
    def apply(): Application = {
      val env = Environment.simple(mode = Mode.Prod)
      val context = ApplicationLoader.createContext(env)

      AppLoader.load(context)
    }

    private object AppLoader extends ApplicationLoader {
      override def load(context: Context): Application =
        new ApplicationComponents(context).application


      private class ApplicationComponents(context: Context) extends BuiltInComponentsFromContext(context) with I18nComponents {
        override lazy val router = Router.empty

        override lazy val httpErrorHandler = new MockedErrorHandler(appConfiguration, messagesApi, environment, sourceMapper, Some(router))

        lazy val appConfiguration = new Configuration {
          override val identityApiHost: String = "identityApiHost"
          override val identityApiKey: String = "identityApiKey"
          override val identityCookieDomain: String = "theguardian.com"
          override val identityProfileBaseUrl: String = "profile.theguardian.com"
          override val omnitureAccount: String = "omnitureAccount"
          override val appConfiguration = configuration
          override val identityFederationApiHost: String = "https://oauth.theguardian.com"
        }
      }
    }
  }

}
