/*global window, document*/

import { getElementById, sessionStorage } from '../browser/browser';

export function init() {
  const form = getElementById( 'signin_form' );
  const emailField = getElementById( 'signin_field_email' );

  const STORAGE_KEY = 'gu_id_state';

  if (!form) { throw new Error( 'Could not find signin form' ); }

  form.on( 'submit', () => {
    const email = emailField.value();

    if ( email && email.length > 0 ) {
      sessionStorage.set( STORAGE_KEY, JSON.stringify( { email } ) );
    }
  } );

  if ( !emailField.value() ) {
    const existingState = sessionStorage.get( STORAGE_KEY );

    if ( existingState ) {
      const parsedState = JSON.parse( existingState );

      if ( parsedState && parsedState.email ) {
        emailField.setValue( parsedState.email );
      }
    }
  }
}
