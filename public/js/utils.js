/* @flow */
/* eslint import/prefer-default-export: 0 */

export const withPolyfill = (success: () => void, error: () => void) => {
  if (window.fetch && window.Promise) {
    success();
  } else {
    const errorTimeout = window.setTimeout(() => {
      error();
    }, 5000);
    const $script = document.createElement('script');
    $script.addEventListener('load', () => {
      clearTimeout(errorTimeout);
      success();
    });
    $script.src =
      'https://assets.guim.co.uk/polyfill.io/v2/polyfill.min.js?rum=0&features=es6,es7,es2017,Object.assign,Element.prototype.dataset,default-3.6,fetch,promise&flags=gated&callback=onPolyfillIo&unknown=polyfill';
    if (document.body) {
      document.body.appendChild($script);
    }
  }
};
