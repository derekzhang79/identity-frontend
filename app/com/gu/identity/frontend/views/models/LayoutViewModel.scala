package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.configuration.Configuration
import controllers.routes
import play.api.i18n.Messages
import play.api.libs.json.Json


case class LayoutViewModel(inlineJsConfig: InlineSource, styles: Seq[String], javascripts: Seq[String]) extends ViewModel {
  def toMap(implicit messages: Messages) =
    Map("inlineJsConfig" -> inlineJsConfig.toMap, "styles" -> styles, "javascripts" -> javascripts, "favicons" -> LayoutFaviconsViewModel.icons)
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

object LayoutFaviconsViewModel {
  case class Favicon(filename: String, rel: String, url: String, sizes: Option[String] = None)

  private val iconFiles = Seq(
    "32x32.ico",
    "57x57.png",
    "72x72.png",
    "114x114.png",
    "120x120.png",
    "144x144.png",
    "152x152.png"
  )

  private val iconBaseDir = "components/favicons"

  private val iconUrls = iconFiles.map(f => f -> routes.Assets.at(s"$iconBaseDir/$f").url)

  val icons = iconUrls.map {
    case (file, url) if file.endsWith(".png") => Favicon(file, "apple-touch-icon", url)
    case (file, url) => Favicon(file, "shortcut icon", url)
  }
}
