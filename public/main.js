import './components/sentry/sentry';

import { isSupported as isBrowserSupported } from './components/browser/browser';

import { logPageView } from './components/analytics/analytics';

import { init as initSigninBindings } from './components/signin-form/signin-form';

import { init as initRegisterBindings } from './components/register-form/register-form';


if ( isBrowserSupported ) {

  logPageView();

  initSigninBindings();

  initRegisterBindings();

}
