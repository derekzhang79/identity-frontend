import { loadComponents } from 'js/load-components';
import { withPolyfill } from 'js/utils';

import './components/sentry/sentry';

import { isSupported as isBrowserSupported } from './components/browser/browser';

import { logPageView } from './components/analytics/analytics';

import { init as initSigninBindings } from './components/signin-form/signin-form';

import { init as initRegisterBindings } from './components/register-form/register-form';

withPolyfill(() => {
  logPageView();
  loadComponents(document);
});

if (isBrowserSupported) {
  initSigninBindings();
  initRegisterBindings();
}
