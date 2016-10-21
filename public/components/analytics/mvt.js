/**
 * Multi-variant testing.
 */

import { runtimeParameters } from '../configuration/configuration';

import cookies from 'cookie-cutter';
import { MultiVariantTest } from './mvt-model';


const MULTIVARIATE_ID_COOKIE = 'GU_mvt_id',

  AB_PREFIX = 'ab',
  AB_TEST_NAMESPACE = 'Identity',

  allTests = MultiVariantTest.initFromPageConfig();

function getMvtId() {
  return parseInt(cookies.get(MULTIVARIATE_ID_COOKIE), 10);
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
  if ( runtimeParameters && typeof ( runtimeParameters.activeTests) === 'object') {
    return runtimeParameters.activeTests;
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

function mergeObjects(first, second) {
  const merged = {};

  Object.keys(first).forEach(key => merged[key] = first[key]);
  Object.keys(second).forEach(key => merged[key] = second[key]);

  return merged;
}
