import { isSupported as isBrowserSupported } from './components/browser/browser';

import { logPageView } from './components/analytics/analytics';

import { init as initSigninBindings } from './components/signin/signin';
import { init as initSigninTestB } from './components/signin-b/signin-b';

import { init as initRegisterBindings } from './components/register-form/register-form';


if ( isBrowserSupported ) {

  logPageView();

  initSigninBindings();

  initSigninTestB();

  initRegisterBindings();

}
