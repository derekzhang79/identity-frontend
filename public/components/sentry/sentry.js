/**
 * Use sentry to record Javascript errors
 */

/*global console*/

import Raven from 'raven-js';

import { configuration } from '../configuration/configuration';

function init() {
  const dsn = configuration.sentryDsn;

  if ( typeof dsn === 'string' ) {
    Raven.config( dsn ).install();


  } else if ( console ) {
    console.warn( 'Sentry configuration not found' );
  }
}

init();
