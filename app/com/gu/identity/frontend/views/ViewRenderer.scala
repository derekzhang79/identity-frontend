package com.gu.identity.frontend.views

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.views.models.{ErrorViewModel, LayoutViewModel, SignInViewModel}
import jp.co.bizreach.play2handlebars.HBS
import play.api.i18n.MessagesApi

/**
 * Adapter for Handlebars view renderer
 */
object ViewRenderer {
  def render(view: String, attributes: Map[String, Any] = Map.empty) =
    HBS(view, attributes)

  def renderSignIn(configuration: Configuration, errorIds: Seq[String], email: String)(implicit messages: MessagesApi) = {
    val errors = errorIds.map(ErrorViewModel.apply)
    val attrs = LayoutViewModel(configuration).toMap ++ SignInViewModel(errors = errors, email = email).toMap
    render("signin-page", attrs)
  }
}
