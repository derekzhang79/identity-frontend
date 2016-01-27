package com.gu.identity.frontend.mvt


object TestResults {
  import MultiVariantTests._

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
