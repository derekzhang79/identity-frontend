package com.gu.identity.frontend.mvt

import com.gu.identity.frontend.controllers.NoCache
import play.api.mvc.{ActionBuilder, Request, Result, WrappedRequest}

import scala.concurrent.Future
import scala.util.Try

case class MultiVariantTestRequest[A](mvtCookie: Option[Int], activeTests: Map[MultiVariantTest, MultiVariantTestVariant], request: Request[A]) extends WrappedRequest[A](request)

object MultiVariantTestRequest {
  private val OVERRIDE_PARAM_PREFIX = "mvt_"

  def apply[A](request: Request[A]): MultiVariantTestRequest[A] = {
    val mvtCookie = getMvtCookie(request)
    val activeTests = mvtCookie.map(id => MultiVariantTests.activeTests(id)).getOrElse(Nil).toMap

    MultiVariantTestRequest[A](mvtCookie, activeTests ++ getTestOverrides(request.queryString), request)
  }

  /**
   * Retrieve test overrides from the request parameters. Use ?mvt_<testName>=<variantId>
   */
  private def getTestOverrides(queryString: Map[String, Seq[String]]) = for {
    (key, values) <- queryString
    if key.startsWith(OVERRIDE_PARAM_PREFIX)
    testName = key.substring(OVERRIDE_PARAM_PREFIX.length)
    test <- MultiVariantTests.allServerSide.find(_.name.equalsIgnoreCase(testName))
    overrideValue <- values.headOption
    variant <- test.variants.find(_.id.equalsIgnoreCase(overrideValue))
  } yield test -> variant

  private def getMvtCookie[A](req: Request[A]) =
    req.cookies.get(MultiVariantTests.MVT_COOKIE_NAME).flatMap { mvtId =>
      Try(Integer.parseInt(mvtId.value)).toOption
    }
}

object MultiVariantTestAction extends ActionBuilder[MultiVariantTestRequest] {
  override def invokeBlock[A](request: Request[A], block: (MultiVariantTestRequest[A]) => Future[Result]): Future[Result] = {
    val req = MultiVariantTestRequest[A](request)
    MultiVariantTests.allServerSide.headOption.map { _ =>
      NoCache(block(req))
    }.getOrElse {
      block(req)
    }
  }
}
