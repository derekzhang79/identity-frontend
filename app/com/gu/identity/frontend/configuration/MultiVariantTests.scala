package com.gu.identity.frontend.configuration

/**
 * Define a Multi Variant Test for testing different logic on Users to
 * determine which is best based on the user's behaviour.
 *
 * To define a test, create a {{case object}} which extends from
 * {{MultiVariantTest}} and add it to {{MultiVariantTests.all}}.
 *
 * For example:
 * {{{
 * case object MyABTest extends MultiVariantTest {
 *   val name = "MyAB"
 *   val audience = 0.2
 *   val audienceOffset = 0.6
 *   val isServerSide = true
 *   val variants = Seq(MyABTestVariantA, MyABTestVariantB)
 * }
 *
 * case object MyABTestVariantA extends MultiVariantTestVariant { val id = "A" }
 * case object MyABTestVariantB extends MultiVariantTestVariant { val id = "B" }
 *
 * object MultiVariantTests {
 *   def all: Set[MultiVariantTest] = Set(MyABTest)
 * }
 * }}}
 * Which creates a test with two variants against 20% of the audience, using
 * the segment of users with ids from 60% to 80% of the population.
 */
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


sealed trait MultiVariantTestVariant {
  val id: String
}



case object SignInV2Test extends MultiVariantTest {
  val name = "SignInV2"
  val audience = 1.0
  val audienceOffset = 0.0
  val isServerSide = true
  val variants = Seq(SignInV2TestVariantA, SignInV2TestVariantB)
}

case object SignInV2TestVariantA extends MultiVariantTestVariant { val id = "A" }
case object SignInV2TestVariantB extends MultiVariantTestVariant { val id = "B" }



/**
 * Define a MVT at runtime - should only be used for tests.
 */
case class RuntimeMultiVariantTest(
  name: String,
  audience: Double,
  audienceOffset: Double,
  isServerSide: Boolean = true,
  variants: Seq[MultiVariantTestVariant]) extends MultiVariantTest

/**
 * Define a MVT variant at runtime - should only be used for tests.
 */
case class RuntimeMultiVariantTestVariant(id: String) extends MultiVariantTestVariant



object MultiVariantTests {

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

  val MVT_COOKIE_NAME = "GU_mvt_id"
  val MAX_ID = 899999

  def all: Set[MultiVariantTest] = Set(SignInV2Test)

  def allActive = all.filter(_.active)

  def allServerSide = allActive.filter(_.isServerSide)

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

  /**
   * Retrieve active server-side tests and the selected variant for an mvtId.
   */
  def activeTests(mvtId: Int, maxId: Int = MAX_ID): Set[(MultiVariantTest, MultiVariantTestVariant)] =
    allServerSide.flatMap { test =>
      activeVariantForTest(test, mvtId, maxId).map(test -> _)
    }
}
