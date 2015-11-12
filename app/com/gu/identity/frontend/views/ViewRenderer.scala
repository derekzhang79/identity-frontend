package com.gu.identity.frontend.views

import jp.co.bizreach.play2handlebars.HBS

/**
 * Adapter for Handlebars view renderer
 */
object ViewRenderer {
  def render(view: String) =
    HBS(view)

  def renderMain =
    render("main")
}
