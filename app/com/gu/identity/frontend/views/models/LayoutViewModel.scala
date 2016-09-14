package com.gu.identity.frontend.views.models

import buildinfo.BuildInfo
import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.models.{ClientID, GuardianJobsClientID, GuardianMembersClientID, ReturnUrl}
import com.gu.identity.frontend.models.Text.{HeaderText, LayoutText}
import com.gu.identity.frontend.models.text.FooterText
import com.gu.identity.frontend.mvt
import com.gu.identity.frontend.mvt.{ActiveMultiVariantTests, MultiVariantTests, MultiVariantTest}
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
    IndirectlyLoadedExternalResources("https://app.getsentry.com/api/"),
    IndirectlyLoadedExternalImageResources("https://hits-secure.theguardian.com"),
    IndirectlyLoadedExternalImageResources("https://sb.scorecardresearch.com"),
    IndirectlyLoadedExternalImageResources("https://ophan.theguardian.com")
  )
}


case class LayoutViewModel private(
    text: Map[String,String],
    headerText: Map[String, String],
    footerText: FooterText,
    links: LayoutLinks,
    resources: Seq[PageResource with Product],
    indirectResources: Seq[PageResource with Product],
    favicons: Seq[Favicon] = Favicons(),
    skin: Option[String])
  extends ViewModel
  with ViewModelResources


/**
 * Config that will be exposed as Javascript inlined into the html response.
 */
case class JavascriptConfig(
    omnitureAccount: String,
    sentryDsn: String,
    mvtTests: Seq[MultiVariantTest],
    appVersion: String = BuildInfo.gitCommitId) {
  self =>

  import mvt.Implicits._
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

  def apply(configuration: Configuration)(implicit messages: Messages): LayoutViewModel =
    apply(configuration, Map.empty, clientId = None, returnUrl = None)

  def apply(configuration: Configuration, clientId: Option[ClientID], returnUrl: Option[ReturnUrl])(implicit messages: Messages): LayoutViewModel =
    apply(configuration, activeTests = Map.empty, clientId, returnUrl)

  def apply(configuration: Configuration, activeTests: ActiveMultiVariantTests, clientId: Option[ClientID], returnUrl: Option[ReturnUrl])(implicit messages: Messages): LayoutViewModel = {

    val skin = clientId
      .filter(_.hasSkin)
      .map(c => s"skin-${c.id}")

    val config = JavascriptConfig(
      omnitureAccount = configuration.omnitureAccount,
      sentryDsn = configuration.sentryDsnJs,
      mvtTests = MultiVariantTests.all.toSeq
    )

    val runtime = activeTests.headOption.map { _ =>
      JavascriptRuntimeParams(activeTests.map {
        case (key, value) => key.name -> value.id
      })
    }

    val inlinedJSConfig = InlinedJSONResource("id_config", config.toJSONString)
    val inlinedJSRuntimeParams = runtime.map { r =>
      InlinedJSONResource("id_runtime_params", r.toJSONString)
    }

    val resources: Seq[PageResource with Product] = BaseLayoutViewModel.resources ++ Seq(Some(inlinedJSConfig), inlinedJSRuntimeParams).flatten

    LayoutViewModel(
      text = LayoutText.toMap,
      headerText = HeaderText.toMap,
      footerText = FooterText(),
      links = LayoutLinks(configuration, clientId, returnUrl),
      resources = resources,
      indirectResources = BaseLayoutViewModel.indirectResources,
      skin = skin)
  }
}

case class LayoutLinks private(
    headerBack: String,
    headerLogo: String,
    footerHelp: String,
    footerTerms: String,
    footerContact: String,
    footerPrivacy: String,
    footerFeedback: String,
    footerCookies: String)

object LayoutLinks {
  def apply(configuration: Configuration, clientId: Option[ClientID], returnUrl: Option[ReturnUrl]): LayoutLinks = {
    val baseUrl = configuration.dotcomBaseUrl

    LayoutLinks(
      headerBack = returnUrl.map(_.url).getOrElse(baseUrl),
      headerLogo = logoUrl(configuration, clientId),
      footerHelp = s"$baseUrl/help/identity-faq",
      footerTerms = s"$baseUrl/help/terms-of-service",
      footerContact = s"$baseUrl/help/contact-us",
      footerPrivacy = s"$baseUrl/info/privacy",
      footerFeedback = s"$baseUrl/info/tech-feedback",
      footerCookies = s"$baseUrl/info/cookies"
    )
  }

  private def logoUrl(configuration: Configuration, clientId: Option[ClientID]) =
    clientId match {
      case Some(GuardianMembersClientID) => configuration.membershipBaseUrl
      case Some(GuardianJobsClientID) => configuration.jobsBaseUrl
      case _ => configuration.dotcomBaseUrl
    }

}

case class Favicon(filename: String, rel: String, url: String, sizes: Option[String] = None)

object Favicons {

  import LocalResource.resolveAssetUrl

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

  private val iconUrls = iconFiles.map(f => f -> resolveAssetUrl(s"$iconBaseDir/$f"))

  def apply(): Seq[Favicon] = iconUrls.map {
    case (file, url) if file.endsWith(".png") => Favicon(file, "apple-touch-icon", url)
    case (file, url) => Favicon(file, "shortcut icon", url)
  }
}
