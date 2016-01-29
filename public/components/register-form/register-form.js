
import { getElementById, sessionStorage } from '../browser/browser';

import { mapValues as _mapValues } from '../lib/lodash';

const STORAGE_KEY = 'gu_id_register_state';


class RegisterFormFields {
  constructor(firstNameField, lastNameField, emailField, usernameField) {
    this.firstName = firstNameField;
    this.lastName = lastNameField;
    this.email = emailField;
    this.username = usernameField;
  }

  setValues( { firstName, lastName, email, username } = {} ) {
    this.firstName.setValue( firstName );
    this.lastName.setValue( lastName );
    this.email.setValue( email );
    this.username.setValue( username );
  }

  mapValues( callback ) {
    return _mapValues( this, callback );
  }

  toJSON() {
    return this.mapValues( field => field.value() );
  }
}


class RegisterFormModel {
  constructor( formElement, firstNameField, lastNameField, emailField, usernameField ) {
    this.formElement = formElement;

    this.fields = new RegisterFormFields( firstNameField, lastNameField, emailField, usernameField );

    this.addBindings();
  }

  addBindings() {
    this.formElement.on( 'submit', this.formSubmitted.bind( this ) );
  }

  loadState() {
    this.state = RegisterFormState.fromStorage();
    this.fields.setValues( this.state );
  }

  saveState() {
    this.state = RegisterFormState.fromForm( this );
    this.state.save();
  }

  formSubmitted() {
    this.saveState();
  }

  static fromDocument() {
    const form = getElementById( 'register_form' );
    const firstNameField = getElementById( 'register_field_firstname' );
    const lastNameField = getElementById( 'register_field_lastname' );
    const emailField = getElementById( 'register_field_email' );
    const usernameField = getElementById( 'register_field_username' );

    if ( form && firstNameField && lastNameField && emailField && usernameField ) {
      return new RegisterFormModel( form, firstNameField, lastNameField, emailField, usernameField );
    }
  }
}


class RegisterFormState {
  constructor( firstName = "", lastName = "", email = "", username = "" ) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.username = username;
  }

  save() {
    sessionStorage.setJSON( STORAGE_KEY, this );
  }

  /**
   * @return {RegisterFormState}
   */
  static fromObject( { firstName, lastName, email, username } = {} ) {
    return new RegisterFormState( firstName, lastName, email, username );
  }

  /**
   * @return {RegisterFormState}
   */
  static fromStorage() {
    const existingState = sessionStorage.getJSON( STORAGE_KEY );

    return RegisterFormState.fromObject( existingState )
  }

  /**
   * @param {RegisterFormModel} form
   * @return {RegisterFormState}
   */
  static fromForm( form ) {
    return RegisterFormState.fromObject( form.fields.toJSON() );
  }
}


export function init() {
  const form = RegisterFormModel.fromDocument();

  if ( form ) {
    form.loadState();
  }

  return form;
}
