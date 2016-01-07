package com.gu.identity.frontend.configuration

sealed trait MultiVariantTest {

  /**
   * Name of test recorded in Analytics results. Automatically prefixed with
   * "ab".
   */
  val name: String

  /**
   * Multiplier to determine portion of audience to select. Should be between
   * 0 and 1.
   */
  val audience: Double

  /**
   * Multiplier to determine offset of audience to start from when partitioning.
   * Should be between 0 and 1.
   */
  val audienceOffset: Double

  /**
   * Flag enabling a test. Can be overridden to only enable a test for a
   * period of time.
   */
  def active: Boolean = true

  /**
   * Mark a test as server-side only. Server-side tests should only be used
   * for non-cacheable routes.
   */
  val isServerSide: Boolean

  /**
   * Variants available to the test which will be exposed to user's in the test.
   */
  val variants: Seq[MultiVariantTestVariant]
}


object MultiVariantTest {

  import play.api.libs.json._
  import MultiVariantTestVariant.{jsonWrites => mvtVariantJsonWrites}

  implicit val jsonWrites: Writes[MultiVariantTest] = new Writes[MultiVariantTest] {
    override def writes(o: MultiVariantTest): JsValue = Json.obj(
      "name" -> o.name,
      "audience" -> o.audience,
      "audienceOffset" -> o.audienceOffset,
      "variants" -> o.variants
    )
  }
}


/**
 * Define a MVT at runtime - should only be used for tests.
 */
case class RuntimeMultiVariantTest(
  name: String,
  audience: Double,
  audienceOffset: Double,
  isServerSide: Boolean = true,
  variants: Seq[MultiVariantTestVariant]) extends MultiVariantTest


sealed trait MultiVariantTestVariant {
  val id: String
}

object MultiVariantTestVariant {
  import play.api.libs.json._

  implicit val jsonWrites: Writes[MultiVariantTestVariant] = new Writes[MultiVariantTestVariant] {
    override def writes(o: MultiVariantTestVariant): JsValue = Json.obj(
      "id" -> o.id
    )
  }
}


/**
 * Define a MVT variant at runtime - should only be used for tests.
 */
case class RuntimeMultiVariantTestVariant(id: String) extends MultiVariantTestVariant


case object SignInV2Test extends MultiVariantTest {
  val name = "SignInV2"
  val audience = 1.0
  val audienceOffset = 0.0
  val isServerSide = true
  val variants = Seq(SignInV2TestVariantA, SignInV2TestVariantB)
}

case object SignInV2TestVariantA extends MultiVariantTestVariant { val id = "A" }
case object SignInV2TestVariantB extends MultiVariantTestVariant { val id = "B" }


object MultiVariantTests {
  val MVT_COOKIE_NAME = "GU_mvt_id"
  val MAX_ID = 899999

  def all = Set[MultiVariantTest](SignInV2Test).filter(_.active)

  def allServerSide = all.filter(_.isServerSide)

  def isInTest(test: MultiVariantTest, mvtId: Int, maxId: Int = MAX_ID): Boolean = {
    val minBound = maxId * test.audienceOffset
    val maxBound = minBound + maxId * test.audience

    minBound < mvtId && mvtId <= maxBound
  }

  def activeVariantForTest(test: MultiVariantTest, mvtId: Int, maxId: Int = MAX_ID): Option[MultiVariantTestVariant] = {
    if (isInTest(test, mvtId, maxId))
      Some(test.variants(mvtId % test.variants.size))

    else None
  }

  def activeTests(mvtId: Int, maxId: Int = MAX_ID): Set[(MultiVariantTest, MultiVariantTestVariant)] =
    allServerSide.flatMap { test =>
      activeVariantForTest(test, mvtId, maxId).map(test -> _)
    }
}
