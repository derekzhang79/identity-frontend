package com.gu.identity.frontend


package object mvt {

  object Implicits {
    import play.api.libs.json._

    implicit val mvtVariantJsonWrites: Writes[MultiVariantTestVariant] = new Writes[MultiVariantTestVariant] {
      override def writes(o: MultiVariantTestVariant): JsValue = Json.obj(
        "id" -> o.id
      )
    }

    implicit val mvtJsonWrites: Writes[MultiVariantTest] = new Writes[MultiVariantTest] {
      override def writes(o: MultiVariantTest): JsValue = Json.obj(
        "name" -> o.name,
        "audience" -> o.audience,
        "audienceOffset" -> o.audienceOffset,
        "isServerSide" -> o.isServerSide,
        "variants" -> o.variants
      )
    }
  }

}
