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
   * Variants available to the test which will be exposed to user's in the test.
   */
  val variants: Seq[MultiVariantTestVariant]
}


/**
 * Define a MVT at runtime - should only be used for tests.
 */
case class RuntimeMultiVariantTest(
  name: String,
  audience: Double,
  audienceOffset: Double,
  variants: Seq[MultiVariantTestVariant]) extends MultiVariantTest


sealed trait MultiVariantTestVariant {
  val id: String
}


/**
 * Define a MVT variant at runtime - should only be used for tests.
 */
case class RuntimeMultiVariantTestVariant(id: String) extends MultiVariantTestVariant


case object SignInV2Test extends MultiVariantTest {
  val name = "SignInV2"
  val audience = 1.0
  val audienceOffset = 0.0
  val variants = Seq(SignInV2TestVariantA, SignInV2TestVariantB)
}

case object SignInV2TestVariantA extends MultiVariantTestVariant { val id = "A" }
case object SignInV2TestVariantB extends MultiVariantTestVariant { val id = "B" }


object MultiVariantTests {
  val MAX_ID = 899999

  val all = Set(SignInV2Test)

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

  def activeTests(mvtId: Int, maxId: Int = MAX_ID) =
    all.map(isInTest(_, mvtId, maxId))
}
