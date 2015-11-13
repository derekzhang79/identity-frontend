package com.gu.identity.frontend.views

import jp.co.bizreach.play2handlebars.HBS

/**
 * Adapter for Handlebars view renderer
 */
object ViewRenderer {
  def render(view: String, attributes: Map[String, Any] = Map.empty) =
    HBS(view, attributes)

  def renderSignIn =
    render("signIn", Map("title" -> "Sign in to the Guardian", "pageTitle" -> "Sign in") )
}
