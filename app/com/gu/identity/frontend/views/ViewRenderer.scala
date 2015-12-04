package com.gu.identity.frontend.views

import com.gu.identity.frontend.views.models.{ErrorViewModel, LayoutViewModel, SignInViewModel}
import jp.co.bizreach.play2handlebars.HBS

/**
 * Adapter for Handlebars view renderer
 */
object ViewRenderer {
  def render(view: String, attributes: Map[String, Any] = Map.empty) =
    HBS(view, attributes)

  def renderSignIn(errorIds: Seq[String], email: String) = {
    val errors = errorIds.map(ErrorViewModel.apply)
    val attrs = LayoutViewModel().toMap ++ SignInViewModel(errors = errors, email = email).toMap
    render("signin-page", attrs)
  }
}
