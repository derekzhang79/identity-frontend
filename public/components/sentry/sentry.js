import { configuration } from '../configuration/configuration';

/**
 * Use sentry to record Javascript errors
 */

/*global console*/

import('raven-js').then(ravenJs => {
  const Raven = ravenJs.default;

  function init() {
    const ravenOptions = {
      whitelistUrls: [/ophan\.co\.uk/],
      release: configuration.appVersion
    };

    if (typeof dsn === 'string') {
      Raven.config(dsn, ravenOptions).install();
    } else if (console) {
      console.warn('Sentry configuration not found');
    }
  }

  init();
});
