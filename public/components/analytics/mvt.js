/**
 * Multi-variant testing.
 */

import cookies from 'cookie-cutter';

const MULTIVARIATE_ID_COOKIE = 'GU_mvt_id',
  VISITOR_ID_COOKIE = 's_vi',
  BROWSER_ID_COOKIE = 'bwid';

function getBrowserId() {
  return cookies.get(BROWSER_ID_COOKIE);
}

function getMvtId() {
  return cookies.get(MULTIVARIATE_ID_COOKIE);
}

function getVisitorId() {
  return cookies.get(VISITOR_ID_COOKIE);
}

/**
 * Retrieves an aggregated ID for Omniture tracking.
 */
export function getMvtFullId() {
  var bwidCookie = getBrowserId(),
    mvtidCookie = getMvtId(),
    visitoridCookie = getVisitorId();

  if (!visitoridCookie) {
    visitoridCookie = 'unknown-visitor-id';
  }

  if (!bwidCookie) {
    bwidCookie = 'unknown-browser-id';
  }

  if (!mvtidCookie) {
    mvtidCookie = 'unknown-mvt-id';
  }

  return visitoridCookie + ' ' + bwidCookie + ' ' + mvtidCookie;
}
