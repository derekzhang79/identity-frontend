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

    setJSON: function setJSONValue( key, value ) {
      return this.set( key, JSON.stringify( value ) );
    },

    get: function getValue( key ) {
      return checkSupported() && storageActual.getItem( key ) || undefined;
    },

    getJSON: function getJSONValue( key ) {
      try {
        const value = this.get(key);

        if ( typeof value === 'string' ) {
          return JSON.parse( value );
        }

      } catch ( err ) {
        if ( console && console.warn ) {
          console.warn( `Error parsing JSON from storage for key "${key}": ${err}` );
        }
      }
    }
  });
}

export const sessionStorage = storage( 'sessionStorage' );
