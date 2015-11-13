package com.gu.identity.frontend.filters

import javax.inject.Inject

import com.mohiva.play.htmlcompressor.HTMLCompressorFilter
import play.api.http.HttpFilters
import play.filters.headers.SecurityHeadersFilter

class Filters @Inject() (securityHeadersFilter: SecurityHeadersFilter, htmlCompressorFilter: HTMLCompressorFilter) extends HttpFilters {
  def filters = Seq(securityHeadersFilter, htmlCompressorFilter)
}
