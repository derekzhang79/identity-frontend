package com.gu.identity.frontend.views.models

import controllers.routes
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils

// Resources loaded directly or indirectly in a page
sealed trait PageResource

sealed trait InlinedResource

sealed trait UnsafeResource

sealed trait InlinedSource {
  val source: String
  val sha256: String
}

object InlinedSource {
  def sha256(in: String) =
    Option(Base64.encodeBase64String(DigestUtils.sha256(in)))
}

sealed trait ExternalResource {
  val domain: String
}

sealed trait LocalResource

object LocalResource {
  def resolveAssetUrl(path: String) =
    routes.Assets.versioned(path).url
}

sealed trait LinkedResource {
  val url: String
}

sealed trait ScriptResource extends PageResource
sealed trait StylesResource extends PageResource
sealed trait ImageResource extends PageResource
sealed trait FontResource extends PageResource
sealed trait FrameResource extends PageResource

sealed trait EmbeddableResource {
  val isJavascript: Boolean = false
  val isCSS: Boolean = false
  val isInHead: Boolean = false
  val isInline: Boolean = false
}



case class LocalJavascriptResource private(
  url: String,
  override val isInHead: Boolean,
  override final val isJavascript: Boolean = true) extends ScriptResource with LinkedResource with EmbeddableResource with LocalResource

object LocalJavascriptResource {
  def fromAsset(path: String, isInHead: Boolean): LocalJavascriptResource =
    LocalJavascriptResource(LocalResource.resolveAssetUrl(path), isInHead = isInHead)
}

case class JavascriptResource (
  url: String,
  domain: String,
  override val isInHead: Boolean,
  override final val isJavascript: Boolean = true) extends ScriptResource with LinkedResource with EmbeddableResource with ExternalResource


case class InlinedJavascriptResource(
  source: String,
  sha256: String,
  override val isInHead: Boolean = false,
  override final val isInline: Boolean = true,
  override final val isJavascript: Boolean = true) extends ScriptResource with InlinedResource with InlinedSource with EmbeddableResource

object InlinedJavascriptResource {
  def apply(source: String): InlinedJavascriptResource =
    apply(source, isInHead = false)

  def apply(source: String, isInHead: Boolean): InlinedJavascriptResource =
    InlinedJavascriptResource(source, InlinedSource.sha256(source).getOrElse(""), isInHead)
}


case class LocalCSSResource private(
  url: String,
  override final val isCSS: Boolean = true) extends StylesResource with LinkedResource with LocalResource with EmbeddableResource

object LocalCSSResource {
  def fromAsset(path: String): LocalCSSResource =
    LocalCSSResource(LocalResource.resolveAssetUrl(path))
}

case object UnsafeInlineCSSResource extends StylesResource with InlinedResource with UnsafeResource

case object IndirectlyLoadedFontResources extends FontResource with LocalResource
case object IndirectlyLoadedInlinedFontResources extends FontResource with InlinedResource
case class IndirectlyLoadedExternalFontResources(domain: String) extends FontResource with ExternalResource

case object IndirectlyLoadedImageResources extends ImageResource with LocalResource
case object IndirectlyLoadedInlinedImageResources extends ImageResource with InlinedResource
case class IndirectlyLoadedExternalImageResources(domain: String) extends ImageResource with ExternalResource

case object IndirectlyLoadedScriptResources extends ScriptResource with LocalResource
case object IndirectlyLoadedInlinedScriptResources extends ScriptResource with InlinedResource
case class IndirectlyLoadedExternalScriptResources(domain: String) extends ScriptResource with ExternalResource

case class IndirectlyLoadedExternalFrameResource(domain: String) extends FrameResource with ExternalResource
