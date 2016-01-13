package com.gu.identity.frontend.configuration

import com.gu.identity.frontend.controllers.{Manifest, RegisterAction, SigninAction, HealthCheck, Application}
import com.gu.identity.frontend.filters.{BetaUserGroupFilter, SecurityHeadersFilter, Filters}
import com.gu.identity.frontend.services.{IdentityServiceRequestHandler, IdentityServiceImpl, IdentityService}
import com.gu.identity.service.client.IdentityClient
import jp.co.bizreach.play2handlebars.HandlebarsPlugin
import play.api.i18n.I18nComponents
import play.filters.gzip.GzipFilter
import router.Routes
import play.api.libs.ws.ning.NingWSComponents
import play.api.{BuiltInComponentsFromContext, ApplicationLoader}
import play.api.ApplicationLoader.Context
import com.mohiva.play.htmlcompressor.DefaultHTMLCompressorFilter

class FrontendApplicationLoader extends ApplicationLoader {
  def load(context: Context) = {
    val app = new ApplicationComponents(context).application
    new HandlebarsPlugin(app)
    app
  }
}

class ApplicationComponents(context: Context) extends BuiltInComponentsFromContext(context) with NingWSComponents with I18nComponents {
  lazy val frontendConfiguration = new ApplicationConfiguration(configuration)

  lazy val identityServiceRequestHandler = new IdentityServiceRequestHandler(wsClient)
  lazy val identityClient: IdentityClient = new IdentityClient
  lazy val identityService: IdentityService = new IdentityServiceImpl(frontendConfiguration, identityServiceRequestHandler, identityClient)

  lazy val applicationController = new Application(frontendConfiguration, messagesApi)
  lazy val healthcheckController = new HealthCheck()
  lazy val manifestController = new Manifest()
  lazy val signinController = new SigninAction(identityService, messagesApi)
  lazy val registerController = new RegisterAction(identityService, messagesApi, frontendConfiguration)
  lazy val assets = new controllers.Assets(httpErrorHandler)

  override lazy val httpFilters = new Filters(new SecurityHeadersFilter(
    frontendConfiguration),
    new GzipFilter(),
    new DefaultHTMLCompressorFilter(configuration, environment),
    new BetaUserGroupFilter(frontendConfiguration)
  ).filters

  override lazy val router = new Routes(httpErrorHandler, applicationController, signinController, registerController, healthcheckController, manifestController, assets)
}
