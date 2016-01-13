/**
 * Multi-variant testing.
 */

/*global window*/

import cookies from 'cookie-cutter';
import { MultiVariantTest } from './mvt-model';


const MULTIVARIATE_ID_COOKIE = 'GU_mvt_id',
  VISITOR_ID_COOKIE = 's_vi',
  BROWSER_ID_COOKIE = 'bwid',

  AB_PREFIX = 'ab',
  AB_TEST_NAMESPACE = 'Identity',

  allTests = MultiVariantTest.initFromPageConfig();

function getBrowserId() {
  return cookies.get(BROWSER_ID_COOKIE);
}

function getMvtId() {
  return parseInt(cookies.get(MULTIVARIATE_ID_COOKIE), 10);
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
  const serverSideResults = getServerSideActiveTestResults(),
    clientSideResults = getClientSideActiveTestResults();

  return mergeObjects(serverSideResults, clientSideResults);
}

function getServerSideActiveTestResults() {
  if (window._idRuntimeParams && typeof (window._idRuntimeParams.activeTests) === 'object') {
    return window._idRuntimeParams.activeTests;
  }
  return {};
}

function getClientSideActiveTestResults() {
  const mvtId = getMvtId(),

    activeTests = allTests.filter(t => !t.isServerSide && t.isInTest(mvtId)),

    out = {};

  activeTests.forEach(t => out[t.name] = t.activeVariant(mvtId));

  return out;
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

function mergeObjects(first, second) {
  const merged = {};

  Object.keys(first).forEach(key => merged[key] = first[key]);
  Object.keys(second).forEach(key => merged[key] = second[key]);

  return merged;
}
