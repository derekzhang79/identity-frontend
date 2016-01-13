import { isSupported as isBrowserSupported } from './components/browser/browser';

import { logPageView } from './components/analytics/analytics';

import { init as initSigninBindings } from './components/signin/signin';
import { init as initSigninTestB } from './components/signin-b/signin-b';


if ( isBrowserSupported ) {

  logPageView();

  initSigninBindings();

  initSigninTestB();

}
