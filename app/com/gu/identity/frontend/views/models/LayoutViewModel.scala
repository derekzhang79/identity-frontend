package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.configuration.Configuration
import controllers.routes
import play.api.i18n.MessagesApi
import play.api.libs.json.Json


case class LayoutViewModel(inlineJsConfig: InlineSource, styles: Seq[String], javascripts: Seq[String]) extends ViewModel {
  def toMap(implicit messages: MessagesApi) =
    Map("inlineJsConfig" -> inlineJsConfig.toMap, "styles" -> styles, "javascripts" -> javascripts)
}

object LayoutViewModel {

  val styleFiles = Seq("bundle.css")
  val javascriptFiles = Seq("main.bundle.js")

  def apply(configuration: Configuration): LayoutViewModel = {
    val styleUrls = styleFiles.map(routes.Assets.at(_).url)
    val javascriptUrls = javascriptFiles.map(routes.Assets.at(_).url)

    val config = Map(
      "omnitureAccount" -> configuration.omnitureAccount
    )

    val jsConfig = Json.stringify(Json.toJson(config))
    val jsConfigScript = s"""this._idConfig=$jsConfig;"""

    LayoutViewModel(InlineSource(jsConfigScript), styleUrls, javascriptUrls)
  }
}
