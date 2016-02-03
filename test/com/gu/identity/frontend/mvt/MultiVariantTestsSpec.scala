package com.gu.identity.frontend.mvt

import com.gu.identity.frontend.mvt.TestResults._
import org.scalatest.{Matchers, WordSpec}


class MultiVariantTestsSpec extends WordSpec with Matchers {

  def testMvtID = MultiVariantTestID(_: Int, maxId = 100)

  case class MockedMVT(
      name: String,
      audience: Double,
      audienceOffset: Double,
      variants: Seq[MultiVariantTestVariant],
      isServerSide: Boolean = true)
    extends RuntimeMultiVariantTest

  case class MockedMVTVariant(id: String) extends RuntimeMultiVariantTestVariant


  "A Multi Variant Test" when {

    "determining whether user is in test" should {

      val test = MockedMVT(
        name = "test",
        audience = 0.1,
        audienceOffset = 0.5,
        variants = Seq.empty
      )

      "yield true when in test" in {
        isInTest(test, testMvtID(51)) shouldBe true
        isInTest(test, testMvtID(60)) shouldBe true
      }

      "yield false when below audience offset" in {
        isInTest(test, testMvtID(1)) shouldBe false
        isInTest(test, testMvtID(50)) shouldBe false
      }

      "yield false when outside of audience participation" in {
        isInTest(test, testMvtID(61)) shouldBe false
      }

      "yield false when mvt id > max" in {
        isInTest(test, testMvtID(101)) shouldBe false
      }
    }

    "it has only a single variant" should {
      val variant = MockedMVTVariant("A")

      val test = MockedMVT(
        name = "test",
        audience = 0.1,
        audienceOffset = 0.5,
        variants = Seq(variant)
      )

      "yield variant when in test" in {
        activeVariantForTest(test, testMvtID(51)) shouldEqual Some(variant)
      }

      "yield None when not in test" in {
        activeVariantForTest(test, testMvtID(1)) shouldEqual None
      }
    }

    "it has multiple variants" should {
      val variantA = MockedMVTVariant("A")
      val variantB = MockedMVTVariant("B")
      val variantC = MockedMVTVariant("C")

      "yield correct variant when two variants available" in {
        val test = MockedMVT(
          name = "test",
          audience = 0.1,
          audienceOffset = 0.5,
          variants = Seq(variantA, variantB)
        )

        activeVariantForTest(test, testMvtID(51)) shouldEqual Some(variantB)
        activeVariantForTest(test, testMvtID(52)) shouldEqual Some(variantA)
        activeVariantForTest(test, testMvtID(53)) shouldEqual Some(variantB)
        activeVariantForTest(test, testMvtID(54)) shouldEqual Some(variantA)
      }

      "yield correct variant when three variants available" in {
        val test = MockedMVT(
          name = "test",
          audience = 0.1,
          audienceOffset = 0.5,
          variants = Seq(variantA, variantB, variantC)
        )

        activeVariantForTest(test, testMvtID(51)) shouldEqual Some(variantA)
        activeVariantForTest(test, testMvtID(52)) shouldEqual Some(variantB)
        activeVariantForTest(test, testMvtID(53)) shouldEqual Some(variantC)
        activeVariantForTest(test, testMvtID(54)) shouldEqual Some(variantA)
        activeVariantForTest(test, testMvtID(55)) shouldEqual Some(variantB)
        activeVariantForTest(test, testMvtID(56)) shouldEqual Some(variantC)
      }
    }
  }

}
