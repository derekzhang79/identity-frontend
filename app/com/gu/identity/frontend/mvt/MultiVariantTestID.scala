package com.gu.identity.frontend.mvt

import play.api.mvc.RequestHeader

import scala.util.Try

case class MultiVariantTestID(
    id: Int,
    maxId: Int = MultiVariantTestID.MAX_ID)


object MultiVariantTestID {
  val MVT_COOKIE_NAME = "GU_mvt_id"
  val MAX_ID = 899999

  def fromRequest(request: RequestHeader): Option[MultiVariantTestID] =
    request.cookies.get(MVT_COOKIE_NAME).flatMap { mvtId =>
      Try(Integer.parseInt(mvtId.value)).toOption
    }.map(MultiVariantTestID(_))
}
