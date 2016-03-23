/**
 * Use sentry to record Javascript errors
 */

/*global console*/

import Raven from 'raven-js';

import { configuration } from '../configuration/configuration';

function init() {
  const dsn = configuration.sentryDsn;
  const ravenOptions = {
    release: configuration.appVersion
  };

  if ( typeof dsn === 'string' ) {
    Raven.config( dsn, ravenOptions ).install();


  } else if ( console ) {
    console.warn( 'Sentry configuration not found' );
  }
}

init();
