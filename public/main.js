import { isSupported as isBrowserSupported } from './components/browser/browser';

import { logPageView } from './components/analytics/analytics';

import { init as initSigninBindings } from './components/signin/signin';


if ( isBrowserSupported ) {

  logPageView();

  initSigninBindings();

}
