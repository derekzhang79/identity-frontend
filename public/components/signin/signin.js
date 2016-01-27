/*global window, document*/

import { getElementById, sessionStorage } from '../browser/browser';

const STORAGE_KEY = 'gu_id_signIn_state';


class SignInFormModel {
  constructor( formElement, emailField ) {
    this.formElement = formElement;
    this.emailFieldElement = emailField;

    this.state = SignInFormState.fromStorage();
    this.addFormListeners();
  }

  addFormListeners() {
    this.formElement.on( 'submit', this.formSubmitted.bind( this ) );
  }

  loadState() {
    if ( !this.emailFieldElement.value() && this.state.email ) {
      this.emailFieldElement.setValue( this.state.email );
    }
  }

  saveState() {
    const email = this.emailFieldElement.value();

    this.state.save( email );
  }

  formSubmitted() {
    this.saveState();
  }

  static fromDocument() {
    const form = getElementById( 'signin_form' );
    const emailField = getElementById( 'signin_field_email' );

    if ( form && emailField ) {
      return new SignInFormModel( form, emailField );
    }
  }
}


class SignInFormState {
  constructor( email ) {
    this.email = email;
  }

  save( email ) {
    if ( typeof email === 'string' && email.length > 0 ) {
      sessionStorage.setJSON( STORAGE_KEY, { email } );
    }
  }

  static fromStorage() {
    const existingState = sessionStorage.getJSON( STORAGE_KEY );

    const email = typeof existingState === 'object' && existingState.email || undefined;

    return new SignInFormState( email );
  }
}


export function init() {

  const form = SignInFormModel.fromDocument();

  if ( form ) {
    form.loadState();
  }

}
