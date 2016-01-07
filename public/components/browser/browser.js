/*global window, document*/

import { domElement } from './dom-element';

export { sessionStorage } from './storage';

/**
 * Basic browser feature detection.
 * @returns {boolean}
 */
export const isSupported =
  'querySelector' in document &&
  'addEventListener' in window &&
  'localStorage' in window &&
  'sessionStorage' in window &&
  'bind' in Function;


export function getElementById( id ) {
  return domElement( document.getElementById( id ) );
}
