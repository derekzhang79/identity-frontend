package com.gu.identity.frontend.errors

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.test.SimpleAppPerSuite
import jp.co.bizreach.play2handlebars.HandlebarsPlugin
import play.api.ApplicationLoader.Context
import play.api.i18n.I18nComponents
import play.api.routing.Router
import play.api._
import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.play.PlaySpec


class ErrorHandlerSpec extends PlaySpec with SimpleAppPerSuite {

  override lazy val app: Application = FakeApplicationWithErrorHandler()

  "Error handler" must {

    "display 404 not found error" in {
      val Some(resp) = route(FakeRequest(GET, "/iDoNotExist"))
      val content = contentAsString(resp)

      status(resp) must equal(404)
      content must include("Page not found")
      content must include("Sorry - we have not been able to find the page you asked for")
    }

  }


  object FakeApplicationWithErrorHandler {
    def apply(): Application = {
      val env = Environment.simple(mode = Mode.Prod)
      val context = ApplicationLoader.createContext(env)

      AppLoader.load(context)
    }

    private object AppLoader extends ApplicationLoader {
      override def load(context: Context): Application = {
        val app = new ApplicationComponents(context).application
        new HandlebarsPlugin(app)
        app
      }

      private class ApplicationComponents(context: Context) extends BuiltInComponentsFromContext(context) with I18nComponents {
        override lazy val router = Router.empty

        override lazy val httpErrorHandler = new ErrorHandler(appConfiguration, messagesApi, environment, sourceMapper, Some(router))

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
