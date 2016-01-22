package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.configuration.{MultiVariantTestVariant, MultiVariantTests, MultiVariantTest, Configuration}
import com.gu.identity.frontend.models.Text.{FooterText, HeaderText, LayoutText}
import controllers.routes
import play.api.i18n.Messages
import play.api.libs.json.Json


case object BaseLayoutViewModel extends ViewModel with ViewModelResources {

  val resources: Seq[PageResource with Product] = Seq(
    LocalCSSResource.fromAsset("bundle.css"),
    LocalJavascriptResource.fromAsset("main.bundle.js", isInHead = false)
  )

  val indirectResources: Seq[PageResource with Product] = Seq(
    IndirectlyLoadedInlinedFontResources,
    IndirectlyLoadedFontResources,
    IndirectlyLoadedImageResources,
    IndirectlyLoadedInlinedImageResources,
    IndirectlyLoadedExternalScriptResources("https://j.ophan.co.uk"),
    IndirectlyLoadedExternalImageResources("https://hits-secure.theguardian.com"),
    IndirectlyLoadedExternalImageResources("https://sb.scorecardresearch.com"),
    IndirectlyLoadedExternalImageResources("https://ophan.theguardian.com")
  )
}


case class LayoutViewModel(
    text: Map[String,String],
    headerText: Map[String, String],
    footerText: Map[String, String],
    resources: Seq[PageResource with Product],
    indirectResources: Seq[PageResource with Product],
    favicons: Seq[Favicon] = Favicons())
  extends ViewModel
  with ViewModelResources


/**
 * Config that will be exposed as Javascript inlined into the html response.
 */
case class JavascriptConfig(omnitureAccount: String, mvtTests: Seq[MultiVariantTest]) {
  self =>

  import MultiVariantTests.Implicits._
  implicit val jsonWrites = Json.writes[JavascriptConfig]

  def toJSON =
    Json.toJson(self)

  def toJSONString: String =
    Json.stringify(toJSON)

  def toJavascript: String =
    s"this._idConfig=$toJSONString;"
}

case class JavascriptRuntimeParams(activeTests: Map[String, String]) {
  self =>

  implicit val jsonWrites = Json.writes[JavascriptRuntimeParams]

  def toJSON =
    Json.toJson(self)

  def toJSONString: String =
    Json.stringify(toJSON)

  def toJavascript: String =
    s"this._idRuntimeParams=$toJSONString;"
}

object LayoutViewModel {

  def apply(configuration: Configuration, activeTests: Iterable[(MultiVariantTest, MultiVariantTestVariant)])(implicit messages: Messages): LayoutViewModel = {

    val config = JavascriptConfig(
      omnitureAccount = configuration.omnitureAccount,
      mvtTests = MultiVariantTests.all.toSeq
    )

    val runtime = activeTests.headOption.map { _ =>
      JavascriptRuntimeParams(activeTests.map {
        case (key, value) => key.name -> value.id
      }.toMap)
    }

    val inlinedJSConfig = InlinedJavascriptResource(config.toJavascript, isInHead = true)
    val inlinedJSRuntimeParams = runtime.map { r =>
      InlinedJavascriptResource(r.toJavascript, isInHead = true)
    }

    val resources: Seq[PageResource with Product] = BaseLayoutViewModel.resources ++ Seq(Some(inlinedJSConfig), inlinedJSRuntimeParams).flatten

    LayoutViewModel(
      text = LayoutText.toMap,
      headerText = HeaderText.toMap,
      footerText = FooterText.toMap,
      resources = resources,
      indirectResources = BaseLayoutViewModel.indirectResources)
  }
}

case class Favicon(filename: String, rel: String, url: String, sizes: Option[String] = None)

object Favicons {

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

  private val iconUrls = iconFiles.map(f => f -> routes.Assets.versioned(s"$iconBaseDir/$f").url)

  def apply(): Seq[Favicon] = iconUrls.map {
    case (file, url) if file.endsWith(".png") => Favicon(file, "apple-touch-icon", url)
    case (file, url) => Favicon(file, "shortcut icon", url)
  }
}
