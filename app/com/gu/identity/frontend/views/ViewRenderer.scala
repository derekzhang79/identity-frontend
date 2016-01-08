package com.gu.identity.frontend.views

import com.gu.identity.frontend.configuration.{MultiVariantTestVariant, MultiVariantTest, Configuration}
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

  def renderSignIn(configuration: Configuration, activeTests: Iterable[(MultiVariantTest, MultiVariantTestVariant)], errorIds: Seq[String], returnUrl: ReturnUrl, skipConfirmation: Option[Boolean])(implicit messages: Messages) = {
    val errors = errorIds.map(ErrorViewModel.apply)

    renderViewModel("signin-page", SignInViewModel(
      configuration = configuration,
      errors = errors,
      returnUrl = returnUrl,
      skipConfirmation = skipConfirmation
    ))
  }

  def renderViewModel(view: String, model: ViewModel with ViewModelResources with Product): Result = {
    val html = HBS.withProduct(view, model)

    Results.Ok(html)
      .withHeaders(ContentSecurityPolicy.cspForViewModel(model))
  }

}
