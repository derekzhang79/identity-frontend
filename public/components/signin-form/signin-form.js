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

  smartLockSetupOnSubmit() {

    const formElement = this.formElement.elem;

    if (navigator.credentials) {
      var c = new PasswordCredential(formElement);
      fetch(formElement.action, {credentials: c, method: 'POST'})
        .then(r => {
          if (r.type == 'opaqueredirect' && !r.url.endsWith('/actions/signin')) {
            this.storeRedirect(c);
          }
        });
      }
    }

  smartLock() {
    navigator.credentials.get({
        password: true,
      })
      .then(c => {
        if (c instanceof PasswordCredential) {
          c.additionalData = new FormData(document.querySelector('#signin-form'));
          c.idName = "email";
          fetch("/actions/signin", {credentials: c, method: 'POST'})
            .then(r => {
              if (r.type == 'opaqueredirect') {
                this.storeRedirect(c);
              }
            })
          };
      });
  }

  storeRedirect(c) {
    navigator.credentials.store(c).then(_ => {
      window.location = getElementById('signin_returnUrl').value();
    });
  }

  formSubmitted() {
    this.smartLockSetupOnSubmit();
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
