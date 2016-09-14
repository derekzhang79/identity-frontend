package com.gu.identity.frontend.mvt

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

  /**
   * Defines a default variant to use when a MVT ID cannot be determined.
   * Should only be used in 100% tests.
   */
  val defaultVariant: Option[MultiVariantTestVariant] = None
}


sealed trait MultiVariantTestVariant {
  val id: String
}

/**
 * Define a MVT at runtime - should only be used for tests.
 */
private[mvt] trait RuntimeMultiVariantTest extends MultiVariantTest

/**
 * Define a MVT variant at runtime - should only be used for tests.
 */
private[mvt] trait RuntimeMultiVariantTestVariant extends MultiVariantTestVariant



object MultiVariantTests {

  def all: Set[MultiVariantTest] = Set()

  def allActive = all.filter(_.active)

  def allServerSide = allActive.filter(_.isServerSide)

}
