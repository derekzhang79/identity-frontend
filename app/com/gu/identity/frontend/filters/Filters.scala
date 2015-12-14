package com.gu.identity.frontend.filters

import javax.inject.Inject

import com.mohiva.play.htmlcompressor.HTMLCompressorFilter
import play.api.http.HttpFilters
import play.filters.gzip.GzipFilter

class Filters @Inject() (securityHeadersFilter: SecurityHeadersFilter,
                         gzipFilter: GzipFilter,
                         htmlCompressorFilter: HTMLCompressorFilter,
                         betaUserGroupFilter: BetaUserGroupFilter) extends HttpFilters {
  // Note: filters are processed in reverse (foldRight), so need to explicitly
  //       put htmlCompressorFilter at end so that its processed first
  def filters = Seq(betaUserGroupFilter, StrictTransportSecurityHeaderFilter, securityHeadersFilter, gzipFilter, htmlCompressorFilter)
}
