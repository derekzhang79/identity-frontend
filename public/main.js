import './components/sentry/sentry';

import { loadComponents } from './js/load-components';

import { isSupported as isBrowserSupported } from './components/browser/browser';

import { logPageView } from './components/analytics/analytics';

import { init as initSigninBindings } from './components/signin-form/signin-form';

import { init as initRegisterBindings } from './components/register-form/register-form';

if (window.location.hash === '#no-js') {
  console.error('Ran in lite mode');
  document.querySelector('noscript').outerHTML = document
    .querySelector('noscript')
    .outerHTML.replace(/noscript/g, 'div');
} else {

  logPageView();
  loadComponents(document);

  if (isBrowserSupported) {
    initSigninBindings();
    initRegisterBindings();
  }
}
