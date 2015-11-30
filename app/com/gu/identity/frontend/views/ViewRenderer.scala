package com.gu.identity.frontend.views

import com.gu.identity.frontend.views.models.{LayoutViewModel, SignInViewModel}
import jp.co.bizreach.play2handlebars.HBS

/**
 * Adapter for Handlebars view renderer
 */
object ViewRenderer {
  def render(view: String, attributes: Map[String, Any] = Map.empty) =
    HBS(view, attributes)

  def renderSignIn(errors: Seq[String]) = {
    val attrs = LayoutViewModel().toMap ++ SignInViewModel(errors = errors).toMap
    render("signin-page", attrs)
  }
}
