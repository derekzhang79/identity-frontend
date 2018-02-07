package com.gu.identity.frontend.controllers

import com.gu.identity.frontend.csrf.{CSRFCheck, CSRFConfig}
import com.gu.identity.frontend.errors.RedirectOnError
import com.gu.identity.frontend.logging.{LogOnErrorAction, Logging}
import com.gu.identity.frontend.request.ResendTokenActionRequestBody
import com.gu.identity.frontend.services.{IdentityService, ServiceAction, ServiceActionBuilder}
import play.api.mvc.{Controller, Request}
import play.api.libs.concurrent.Execution.Implicits.defaultContext


case class ResendRepermissionTokenAction(
  identityService: IdentityService,
  csrfConfig: CSRFConfig) extends Controller with Logging {

  val redirectRoute: String = routes.Application.resendRepermissionTokenSent().url

  val ResendRepermissionTokenServiceAction: ServiceActionBuilder[Request] =
    ServiceAction andThen
      RedirectOnError(redirectRoute) andThen
      LogOnErrorAction(logger) andThen
      CSRFCheck(csrfConfig)

  val bodyParser = ResendTokenActionRequestBody.bodyParser

  def resend = ResendRepermissionTokenServiceAction(bodyParser) { request =>

    //WILL THIS ALWAYS GO TO tokenSent even if it fails?
    identityService.resendRepermissionToken(request.body).map { eitherResponse =>
      eitherResponse.right.map { _ =>
        NoCache(SeeOther(routes.Application.resendRepermissionTokenSent().url))
      }
    }
  }

}
