/*global window, document*/

import { getElementById, sessionStorage } from '../browser/browser';

const STORAGE_KEY = 'gu_id_signIn_state';


class SignInFormModel {
  constructor( formElement, emailField ) {
    this.formElement = formElement;
    this.emailFieldElement = emailField;

    this.addBindings();
    this.smartLock();
  }

  addBindings() {
    this.formElement.on( 'submit', this.formSubmitted.bind( this ) );
  }

  loadState() {
    this.state = SignInFormState.fromStorage();
    this.emailFieldElement.setValue( this.state.email );
  }

  saveState() {
    const email = this.emailFieldElement.value();

    this.state = new SignInFormState( email );
    this.state.save( email );
  }

  smartLockSubmit() {

    const formElement = this.formElement.elem

    //formElement.setIdAttribute("id", true)

    console.log(formElement)

    if (navigator.credentials) {
      var c = new PasswordCredential(formElement);
      fetch(formElement.action, {credentials: c, method: 'POST'})
        .then(r => {
        if (r.type == 'opaqueredirect')
      { // If we're redirected, success!
        navigator.credentials.store(c).then(_ => {
          window.location = "http://www.theguardian.com/international";
      })
        ;
      }
    else
      {
        // Do something clever to handle the sign-in error.
      }
    })
      ;
    }

  }

  smartLock() {
  // And then try to grab credentials:
    navigator.credentials.get({
        password: true,
        federated: {
          "providers": [ "https://facebook.com", "https://accounts.google.com" ]
        }
        /*
         Adding `, unmediated: true` here would grab credentials automatically if
         they've allowed that access, and would just return `undefined` without
         asking the user if they haven't.
         */
      })
      .then(c => {
      if (c instanceof PasswordCredential) {
      c.additionalData = new FormData(document.querySelector('#signin-form'));
      c.idName = "email";
      fetch("/actions/signin", { credentials: c, method: 'POST' })
        .then(r => {
        if (r.type == 'opaqueredirect') { // If we're redirected, success!
        navigator.credentials.store(c).then(_ => {
          window.location = "http://www.theguardian.com/international";
      });
      } else {
        // Do something clever to handle the sign-in error.
      }
    });
    }
  });
  }

  formSubmitted() {
    this.smartLockSubmit();
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
  constructor( email = "" ) {
    this.email = email;
  }

  save() {
    sessionStorage.setJSON( STORAGE_KEY, this );
  }

  /**
   * @return {SignInFormState}
   */
  static fromObject( { email } = {} ) {
    return new SignInFormState( email );
  }

  static fromStorage() {
    const existingState = sessionStorage.getJSON( STORAGE_KEY );

    return SignInFormState.fromObject( existingState )
  }
}


export function init() {

  const form = SignInFormModel.fromDocument();

  if ( form ) {
    form.loadState();
  }

}
