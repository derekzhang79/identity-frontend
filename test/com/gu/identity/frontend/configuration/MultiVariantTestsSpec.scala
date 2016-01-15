package com.gu.identity.frontend.configuration

import org.scalatest.{Matchers, WordSpec}
import MultiVariantTests.{activeVariantForTest, isInTest}


class MultiVariantTestsSpec extends WordSpec with Matchers {

  "A Multi Variant Test" when {

    "determining whether user is in test" should {

      val test = RuntimeMultiVariantTest(
        name = "test",
        audience = 0.1,
        audienceOffset = 0.5,
        variants = Seq.empty
      )

      "yield true when in test" in {
        isInTest(test, 51, 100) shouldBe true
        isInTest(test, 60, 100) shouldBe true
      }

      "yield false when below audience offset" in {
        isInTest(test, 1, 100) shouldBe false
        isInTest(test, 50, 100) shouldBe false
      }

      "yield false when outside of audience participation" in {
        isInTest(test, 61, 100) shouldBe false
      }

      "yield false when mvt id > max" in {
        isInTest(test, 101, 100) shouldBe false
      }
    }

    "it has only a single variant" should {
      val variant = RuntimeMultiVariantTestVariant("A")

      val test = RuntimeMultiVariantTest(
        name = "test",
        audience = 0.1,
        audienceOffset = 0.5,
        variants = Seq(variant)
      )

      "yield variant when in test" in {
        activeVariantForTest(test, 51, 100) shouldEqual Some(variant)
      }

      "yield None when not in test" in {
        activeVariantForTest(test, 1, 100) shouldEqual None
      }
    }

    "it has multiple variants" should {
      val variantA = RuntimeMultiVariantTestVariant("A")
      val variantB = RuntimeMultiVariantTestVariant("B")
      val variantC = RuntimeMultiVariantTestVariant("C")

      "yield correct variant when two variants available" in {
        val test = RuntimeMultiVariantTest(
          name = "test",
          audience = 0.1,
          audienceOffset = 0.5,
          variants = Seq(variantA, variantB)
        )

        activeVariantForTest(test, 51, 100) shouldEqual Some(variantB)
        activeVariantForTest(test, 52, 100) shouldEqual Some(variantA)
        activeVariantForTest(test, 53, 100) shouldEqual Some(variantB)
        activeVariantForTest(test, 54, 100) shouldEqual Some(variantA)
      }

      "yield correct variant when three variants available" in {
        val test = RuntimeMultiVariantTest(
          name = "test",
          audience = 0.1,
          audienceOffset = 0.5,
          variants = Seq(variantA, variantB, variantC)
        )

        activeVariantForTest(test, 51, 100) shouldEqual Some(variantA)
        activeVariantForTest(test, 52, 100) shouldEqual Some(variantB)
        activeVariantForTest(test, 53, 100) shouldEqual Some(variantC)
        activeVariantForTest(test, 54, 100) shouldEqual Some(variantA)
        activeVariantForTest(test, 55, 100) shouldEqual Some(variantB)
        activeVariantForTest(test, 56, 100) shouldEqual Some(variantC)
      }
    }
  }

}
