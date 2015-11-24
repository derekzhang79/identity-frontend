package com.gu.identity.frontend.views.models

import controllers.routes


case class LayoutViewModel(styles: Seq[String]) extends ViewModel {
  def toMap =
    Map("styles" -> styles)
}

object LayoutViewModel {

  def apply(): LayoutViewModel = {
    val styles = Seq(routes.Assets.at("layout.css").url)

    LayoutViewModel(styles)
  }
}
