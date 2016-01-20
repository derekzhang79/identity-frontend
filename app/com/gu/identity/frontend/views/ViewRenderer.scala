package com.gu.identity.frontend.views

import com.gu.identity.frontend.configuration._
import com.gu.identity.frontend.models.ReturnUrl
import com.gu.identity.frontend.views.models._
import jp.co.bizreach.play2handlebars.HBS
import play.api.i18n.Messages
import play.api.mvc.{Result, Results}

/**
 * Adapter for Handlebars view renderer
 */
object ViewRenderer {
  def render(view: String, attributes: Map[String, Any] = Map.empty) =
    HBS(view, attributes)

  def renderSignIn(configuration: Configuration, activeTests: Map[MultiVariantTest, MultiVariantTestVariant], errorIds: Seq[String], returnUrl: ReturnUrl, skipConfirmation: Option[Boolean])(implicit messages: Messages) = {
    val errors = errorIds.map(ErrorViewModel.apply)

    val defaultView = "signin-page"
    val view = activeTests.get(SignInV2Test) match {
      case Some(SignInV2TestVariantB) => "signin-page-b"
      case _ => defaultView
    }

    renderViewModel(view, SignInViewModel(
      configuration = configuration,
      activeTests = activeTests,
      errors = errors,
      returnUrl = returnUrl,
      skipConfirmation = skipConfirmation
    ))
  }

  def renderRegisterConfirmation(configuration: Configuration)(implicit messages: Messages) = {
    renderViewModel(
      "register-confirmation",
      RegisterConfirmationViewModel(configuration))
  }

  def renderViewModel(view: String, model: ViewModel with ViewModelResources with Product): Result = {
    val html = HBS.withProduct(view, model)

    Results.Ok(html)
      .withHeaders(ContentSecurityPolicy.cspForViewModel(model))
  }

}
