package com.gu.identity.service.client

sealed trait HttpMethod
case object GET extends HttpMethod
case object POST extends HttpMethod
case object DELETE extends HttpMethod
