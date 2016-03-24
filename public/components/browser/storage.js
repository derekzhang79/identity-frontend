/*global window, console*/

/**
 * Basic abstraction around browser key/value storage, eg: sessionStorage and localStorage.
 *
 * @param {string} type
 * @see https://developer.mozilla.org/en-US/docs/Web/API/Web_Storage_API/Using_the_Web_Storage_API
 */
function storage( type ) {
  const storageActual = window[ type ];

  function isAvailable() {
    return isStorageAvailable( type );
  }

  return Object.freeze({
    set: function setValue( key, value ) {
      if ( isAvailable() ) {
        try {
          storageActual.setItem( key, value );

        } catch ( err ) {
          // Catch all possible errors
          logWarning( `Could not set value in ${type} for key "${key}": ${err}` );
        }
      }
    },

    setJSON: function setJSONValue( key, value ) {
      return this.set( key, JSON.stringify( value ) );
    },

    get: function getValue( key ) {
      return isAvailable() && storageActual.getItem( key ) || undefined;
    },

    getJSON: function getJSONValue( key ) {
      try {
        const value = this.get(key);

        if ( typeof value === 'string' ) {
          return JSON.parse( value );
        }

      } catch ( err ) {
        logWarning( `Error parsing JSON from storage for key "${key}": ${err}` );
      }
    }
  });
}


function isStorageAvailable( storageType ) {
  try {
    var storage = window[ storageType ],
    x = '__storage_test__';
    storage.setItem(x, x);
    storage.removeItem(x);
    return true;

  } catch( errors ) {
    return false;
  }
}


function logWarning( message ) {
  if ( console && console.warn ) {
    console.warn( message );
  }
}

export const sessionStorage = storage( 'sessionStorage' );
