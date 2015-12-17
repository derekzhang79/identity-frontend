/*global window*/

/**
 * Basic abstraction around browser key/value storage, eg: sessionStorage and localStorage.
 *
 * @param {string} type
 */
function storage( type ) {
  const storageActual = window[ type ];

  function checkSupported() {
    if ( !storageActual ) {
      throw new Error( `Could not find supported storage for: ${type}` );
    }

    return true;
  }

  return Object.freeze({
    set: function setValue( key, value ) {
      checkSupported() && storageActual.setItem( key, value );
    },

    get: function getValue( key ) {
      return checkSupported() && storageActual.getItem( key );
    }
  });
}

export const sessionStorage = storage( 'sessionStorage' );
