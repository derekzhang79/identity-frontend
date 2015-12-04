package com.gu.identity.frontend.views.models

import controllers.routes


case class LayoutViewModel(styles: Seq[String], javascripts: Seq[String]) extends ViewModel {
  def toMap =
    Map("styles" -> styles, "javascripts" -> javascripts)
}

object LayoutViewModel {

  val styleFiles = Seq("bundle.css")
  val javascriptFiles = Seq("main.bundle.js")

  def apply(): LayoutViewModel = {
    val styleUrls = styleFiles.map(routes.Assets.at(_).url)
    val javascriptUrls = javascriptFiles.map(routes.Assets.at(_).url)

    LayoutViewModel(styleUrls, javascriptUrls)
  }
}
