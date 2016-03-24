/**
 * Adapter for Ophan API
 */

// Use curl loader as Ophan library is AMD
import 'curl-amd/dist/curl/curl';

import { getActiveTestsAndResultsForOphan } from './mvt';

const ophanScript = 'https://j.ophan.co.uk/ophan.ng.js';

const curlOptions = {
  paths: {
    'ophan/ng': ophanScript
  }
};


function getOphan( retryCount = 1 ) {
  return curl( curlOptions, [ 'ophan/ng' ] )
    .then(
      ophan => ophan,
      error => {
        console.log('here: ' + retryCount + error);
        if ( retryCount <= 2 ) {
          return getOphan( retryCount + 1 );
        } else {
          throw error;
        }
      }
    )
}

export function init() {
  return recordMvt();
}

export function record( obj ) {
  return getOphan().then( ophan => {
    ophan.record( obj );
  });
}

function recordMvt() {
  const params = {
    'abTestRegister': getActiveTestsAndResultsForOphan()
  };

  return record( params );
}
