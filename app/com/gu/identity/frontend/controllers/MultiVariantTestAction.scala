package com.gu.identity.frontend.controllers

import com.gu.identity.frontend.configuration.{MultiVariantTests, MultiVariantTestVariant, MultiVariantTest}
import play.api.mvc.{Result, ActionBuilder, WrappedRequest, Request}

import scala.concurrent.Future
import scala.util.Try

case class MultiVariantTestRequest[A](mvtCookie: Option[Int], activeTests: Map[MultiVariantTest, MultiVariantTestVariant], request: Request[A]) extends WrappedRequest[A](request)

object MultiVariantTestRequest {
  def apply[A](request: Request[A]): MultiVariantTestRequest[A] = {
    val mvtCookie = getMvtCookie(request)
    val activeTests = mvtCookie.map(id => MultiVariantTests.activeTests(id)).getOrElse(Nil).toMap

    MultiVariantTestRequest[A](mvtCookie, activeTests, request)
  }

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
