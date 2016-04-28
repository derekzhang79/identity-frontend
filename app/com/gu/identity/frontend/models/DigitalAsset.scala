package com.gu.identity.frontend.models

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Writes}

trait Target
case class WebTarget(namespace: String, site: String) extends Target
case class AppTarget(namespace: String, packageName: String, fingerprints: List[String]) extends Target

case class WebAsset(relation: Seq[String], target: WebTarget)
case class AppAsset(relation: Seq[String], target: AppTarget)

object DigitalAsset {

  implicit val webTargetWrites: Writes[WebTarget] = (
    (JsPath \ "namespace").write[String] and
      (JsPath \ "site").write[String]
    )(unlift(WebTarget.unapply))

  implicit val appTargetWrites: Writes[AppTarget] = (
    (JsPath \ "namespace").write[String] and
      (JsPath \ "package_name").write[String] and
      (JsPath \ "sha256_cert_fingerprints").write[Seq[String]]
    )(unlift(AppTarget.unapply))

  implicit val webAssetWrites: Writes[WebAsset] = (
    (JsPath \ "relation").write[Seq[String]] and
      (JsPath \ "target").write[WebTarget]
    )(unlift(WebAsset.unapply))

  implicit val appAssetWrites: Writes[AppAsset] = (
    (JsPath \ "relation").write[Seq[String]] and
      (JsPath \ "target").write[AppTarget]
    )(unlift(AppAsset.unapply))

}
