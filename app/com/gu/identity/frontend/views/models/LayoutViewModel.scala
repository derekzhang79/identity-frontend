package com.gu.identity.frontend.views.models

import controllers.routes


case class LayoutViewModel(styles: Seq[String]) extends ViewModel {
  def toMap =
    Map("styles" -> styles)
}

object LayoutViewModel {

  val styleFiles = Seq("main.css")

  def apply(): LayoutViewModel = {
    val styleUrls = styleFiles.map(routes.Assets.at(_).url)

    LayoutViewModel(styleUrls)
  }
}
