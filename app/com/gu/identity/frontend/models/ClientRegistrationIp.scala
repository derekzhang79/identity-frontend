package com.gu.identity.frontend.models

import com.gu.identity.frontend.utils.RemoteAddress
import play.api.mvc.RequestHeader

case class ClientRegistrationIp(ip: String)

object ClientRegistrationIp extends RemoteAddress{
  def apply(request: RequestHeader): ClientRegistrationIp = {
    val ip = clientIp(request).getOrElse("")
    ClientRegistrationIp(ip)
  }
}
