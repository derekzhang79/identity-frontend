package com.gu.identity.frontend.mvt


object TestResults {
  import MultiVariantTests._

  def isInTest(test: MultiVariantTest, mvtId: MultiVariantTestID): Boolean = {
    val minBound = mvtId.maxId * test.audienceOffset
    val maxBound = minBound + mvtId.maxId * test.audience

    minBound < mvtId.id && mvtId.id <= maxBound
  }

  def activeVariantForTest(test: MultiVariantTest, mvtId: MultiVariantTestID): Option[MultiVariantTestVariant] = {
    if (isInTest(test, mvtId))
      Some(test.variants(mvtId.id % test.variants.size))

    else None
  }

  /**
   * Retrieve active server-side tests and the selected variant for an mvtId.
   */
  def activeTests(mvtId: MultiVariantTestID): ActiveMultiVariantTests = (
    for {
      test <- allServerSide
      variant <- activeVariantForTest(test, mvtId)
    } yield test -> variant
  ).toMap


  def activeTests(mvtId: Option[MultiVariantTestID]): ActiveMultiVariantTests =
    mvtId.fold(ifEmpty = activeTestsDefaultVariants)(activeTests)


  def activeTestsDefaultVariants: ActiveMultiVariantTests = (
    for {
      test <- allServerSide
      variant <- test.defaultVariant
    } yield test -> variant
  ).toMap
}
