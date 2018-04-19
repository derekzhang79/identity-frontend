/* eslint-disable */
/**
 * Use sentry to record Javascript errors
 */

/*global console*/

import Raven from 'raven-js';

import { configuration } from '../configuration/configuration';

function init() {
  if (configuration.sentryDsn) Raven.config(configuration.sentryDsn).install();
  else console.warn('Sentry configuration not found');
}

init();
