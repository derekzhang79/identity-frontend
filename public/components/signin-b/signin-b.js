import { getElementById } from '../browser/browser';

export function init() {
  const emailCta = getElementById( 'signin_cta_email' ),
    emailField = getElementById( 'signin_field_email' );

  if ( emailCta && emailField ) {
    emailCta.on( 'click', selectAfterTimeout.bind( null, emailField ) );
  }
}

function selectAfterTimeout( field ) {
  return setTimeout( field.select.bind( field ), 100 );
}
