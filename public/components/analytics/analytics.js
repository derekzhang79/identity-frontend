import omniture from './omniture-adapter';
import { init as initOphan, record as recordOphan } from './ophan';

// Log initial page view events
export function logPageView() {
  omniture.go();

  // for ophan, this will also log the initial page view:
  logMVTResults();
}

const AB_TEST_PREFIX = 'abIdentity';

function getActiveTests() {
  return {
    'SignInV2': 'A'
  };
}

function getActiveTestsWithLogPrefix() {
  const tests = getActiveTests();
  var out = {};

  Object.keys(tests).forEach(key => {
    out[AB_TEST_PREFIX + key] = tests[key];
  });

  return out;
}

function logMVTResults() {
  const params = {
    'abTestRegister': getActiveTestsWithLogPrefix()
  };

  return recordOphan( params );
}
