package com.gu.identity.frontend.configuration

import akka.actor.{ActorRef, Props}
import com.gu.identity.cookie.{IdentityCookieDecoder, IdentityKeys}
import com.gu.identity.frontend.analytics.client.MeasurementProtocolClient
import com.gu.identity.frontend.analytics.{AnalyticsEventActor, EventActor}
import com.gu.identity.frontend.controllers._
import com.gu.identity.frontend.csrf.CSRFConfig
import com.gu.identity.frontend.errors.ErrorHandler
import com.gu.identity.frontend.filters.{Filters, HtmlCompressorFilter, SecurityHeadersFilter}
import com.gu.identity.frontend.logging.{MetricsActor, MetricsLoggingActor, SentryLogging, SmallDataPointCloudwatchLogging}
import com.gu.identity.frontend.services.{GoogleRecaptchaServiceHandler, IdentityService, IdentityServiceImpl, IdentityServiceRequestHandler}
import com.gu.identity.service.client.IdentityClient
import jp.co.bizreach.play2handlebars.HandlebarsPlugin
import play.api.ApplicationLoader.Context
import play.api.i18n.I18nComponents
import play.api.libs.ws.ning.NingWSComponents
import play.api.routing.Router
import play.api.{ApplicationLoader, BuiltInComponentsFromContext, Logger, Mode}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.filters.gzip.GzipFilter
import router.Routes

import scala.concurrent.ExecutionContext

class FrontendApplicationLoader extends ApplicationLoader {
  def load(context: Context) = {
    val app = new ApplicationComponents(context).application
    new HandlebarsPlugin(app)
    app
  }
}

class ApplicationComponents(context: Context) extends BuiltInComponentsFromContext(context) with NingWSComponents with I18nComponents {
  lazy val frontendConfiguration = Configuration(configuration)
  lazy val csrfConfig = CSRFConfig(configuration)

  lazy val identityServiceRequestHandler = new IdentityServiceRequestHandler(wsClient)
  lazy val identityClient: IdentityClient = new IdentityClient
  lazy val identityService: IdentityService = new IdentityServiceImpl(frontendConfiguration, identityServiceRequestHandler, identityClient)

  lazy val measurementProtocolClient: MeasurementProtocolClient = new MeasurementProtocolClient(wsClient)
  lazy val eventActor: ActorRef = actorSystem.actorOf(EventActor.getProps(measurementProtocolClient))
  lazy val analyticsEventActor: AnalyticsEventActor = new AnalyticsEventActor(eventActor)

  lazy val metricsActor: ActorRef = actorSystem.actorOf(Props[MetricsActor])
  lazy val metricsLoggingActor: MetricsLoggingActor = new MetricsLoggingActor(metricsActor)

  lazy val identityCookieDecoder: IdentityCookieDecoder = new IdentityCookieDecoder(IdentityKeys(frontendConfiguration.identityCookiePublicKey))

  lazy val applicationController = new Application(frontendConfiguration, messagesApi, csrfConfig)
  lazy val consentController = new ConsentController(frontendConfiguration, identityService, messagesApi, ExecutionContext.Implicits.global)
  lazy val healthcheckController = new HealthCheck()
  lazy val digitalAssetLinksController = new DigitalAssetLinks(frontendConfiguration)
  lazy val manifestController = new Manifest()
  lazy val cspReporterController = new CSPViolationReporter()
  lazy val googleRecaptchaServiceHandler = new GoogleRecaptchaServiceHandler(wsClient, frontendConfiguration)
  lazy val googleRecaptchaCheck = new GoogleRecaptchaCheck(googleRecaptchaServiceHandler)
  lazy val signinController = new SigninAction(identityService, messagesApi, metricsLoggingActor, analyticsEventActor, csrfConfig, frontendConfiguration)
  lazy val signOutController = new SignOutAction(identityService, messagesApi, frontendConfiguration)
  lazy val registerController = new RegisterAction(identityService, messagesApi, metricsLoggingActor, analyticsEventActor, frontendConfiguration, csrfConfig)
  lazy val thirdPartyTsAndCsController = new ThirdPartyTsAndCs(identityService, frontendConfiguration, messagesApi, httpErrorHandler, identityCookieDecoder.getUserDataForScGuU)
  lazy val resetPasswordController = new ResetPasswordAction(identityService, csrfConfig)
  lazy val assets = new controllers.Assets(httpErrorHandler)
  lazy val redirects = new Redirects

  override lazy val httpFilters = new Filters(new SecurityHeadersFilter(
    frontendConfiguration),
    new GzipFilter(),
    HtmlCompressorFilter(configuration, environment)
  ).filters

  override lazy val httpErrorHandler = new ErrorHandler(frontendConfiguration, messagesApi, environment, sourceMapper, Some(router))

  // Makes sure the logback.xml file is being found in DEV environments
  if (environment.mode == Mode.Dev) {
    Logger.configure(environment)
  }

  if (environment.mode == Mode.Prod) {
    new SmallDataPointCloudwatchLogging(actorSystem).start
  }

  applicationLifecycle.addStopHook(() => {
    metricsLoggingActor.terminateActor()
    analyticsEventActor.terminateActor()
    actorSystem.terminate().map(_ => ())
  })


  override lazy val router: Router = new Routes(httpErrorHandler, applicationController, signOutController,
    thirdPartyTsAndCsController, consentController,  signinController, registerController, resetPasswordController, cspReporterController,
    healthcheckController, digitalAssetLinksController, manifestController, assets, redirects)

  val sentryLogging = new SentryLogging(frontendConfiguration) // don't make it lazy
}
