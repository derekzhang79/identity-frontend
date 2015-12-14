/**
 * Multi-variant testing.
 */

import cookies from 'cookie-cutter';

const MULTIVARIATE_ID_COOKIE = 'GU_mvt_id',
  VISITOR_ID_COOKIE = 's_vi',
  BROWSER_ID_COOKIE = 'bwid',

  AB_PREFIX = 'ab',
  AB_TEST_NAMESPACE = 'Identity';

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


/**
 * Retrieves a map of active test id and the variant result.
 */
function getActiveTestsAndResults() {
  return {
    'SignInV2': 'A'
  };
}


/**
 * Retrieve object used for tracking MVT results in Ophan.
 */
export function getActiveTestsAndResultsForOphan() {
  const tests = getActiveTestsAndResults(),
    out = {};

  Object.keys(tests).forEach( key => {
    out[ AB_PREFIX + AB_TEST_NAMESPACE + key ] = tests[ key ];
  } );

  return out;
}


/**
 * Retrieve string used for tracking MVT results in Omniture.
 *
 * @returns {string} String of the form: AB | <testName> | <variant>,...
 */
export function getActiveTestsAndResultsForOmniture() {
  const tests = getActiveTestsAndResults();

  return Object.keys(tests)
    .map( key => `${AB_PREFIX.toUpperCase()} | ${AB_TEST_NAMESPACE}${key} | ${tests[ key ]}`)
    .join(',');
}

