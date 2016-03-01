package com.gu.identity.frontend.models

import com.gu.identity.frontend.utils.RemoteAddress
import play.api.mvc.RequestHeader

case class ClientIp(ip: String)

object ClientIp extends RemoteAddress {
  def apply(request: RequestHeader): ClientIp = {
    val ip = clientIp(request).getOrElse("")
    ClientIp(ip)
  }
}
