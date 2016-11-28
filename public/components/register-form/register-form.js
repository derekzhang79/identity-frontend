
import { getElementById, sessionStorage } from '../browser/browser';

import { mapValues as _mapValues } from '../lib/lodash';

import { initPhoneField } from '../lib/phone-field';

const STORAGE_KEY = 'gu_id_register_state';


class RegisterFormFields {
  constructor(firstNameField, lastNameField, emailField, displayNameField, optionalCountryCode, optionalCountryIsoName, optionalPhoneNumber) {
    this.firstName = firstNameField;
    this.lastName = lastNameField;
    this.email = emailField;
    this.displayName = displayNameField;
    this.optionalCountryCode = optionalCountryCode;
    this.optionalCountryIsoName = optionalCountryIsoName;
    this.optionalPhoneNumber = optionalPhoneNumber;
  }

  setValues( { firstName, lastName, email, displayName, optionalCountryCode, optionalCountryIsoName, optionalPhoneNumber } = {} ) {
    this.firstName.setValue( firstName );
    this.lastName.setValue( lastName );
    this.email.setValue( email );
    this.displayName.setValue( displayName );
    if (this.optionalPhoneNumber) {
      this.optionalPhoneNumber.setValue(optionalPhoneNumber);
    }
    if (this.optionalCountryCode) {
      this.optionalCountryCode.setValue(optionalCountryCode);
    }
    if (this.optionalCountryIsoName) {
      this.optionalCountryIsoName.setValue(optionalCountryIsoName);
    }
  }

  mapValues( callback ) {
    return _mapValues( this, callback );
  }

  toJSON() {
    return this.mapValues( field => field ? field.value() : undefined );
  }
}


class RegisterFormModel {
  constructor( formElement, firstNameField, lastNameField, emailField, displayNameField, optionalCountryCode, optionalCountryIsoName, optionalPhoneNumber ) {
    this.formElement = formElement;

    this.fields = new RegisterFormFields( firstNameField, lastNameField, emailField, displayNameField, optionalCountryCode, optionalCountryIsoName, optionalPhoneNumber );

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
    const displayNameField = getElementById( 'register_field_displayname' );
    const optionalPhoneNumber = getElementById('register_field_localNumber');
    const optionalCountryCode = getElementById('register_field_countryCode');
    const optionalCountryIsoName = getElementById('register_field_countryIsoName');

    if ( form && firstNameField && lastNameField && emailField && displayNameField ) {
      return new RegisterFormModel( form, firstNameField, lastNameField, emailField, displayNameField, optionalCountryCode, optionalCountryIsoName, optionalPhoneNumber );
    }
  }
}


class RegisterFormState {
  constructor( firstName = "", lastName = "", email = "", displayName = "", optionalCountryCode = "", optionalCountryIsoName = "", optionalPhoneNumber = "" ) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.displayName = displayName;
    this.optionalCountryCode = optionalCountryCode;
    this.optionalCountryIsoName = optionalCountryIsoName;
    this.optionalPhoneNumber = optionalPhoneNumber;
  }

  save() {
    sessionStorage.setJSON( STORAGE_KEY, this );
  }

  /**
   * @return {RegisterFormState}
   */
  static fromObject( { firstName, lastName, email, displayName, optionalCountryCode, optionalCountryIsoName, optionalPhoneNumber } = {} ) {
    return new RegisterFormState( firstName, lastName, email, displayName, optionalCountryCode, optionalCountryIsoName, optionalPhoneNumber );
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
  checkForCredentials();
  const form = RegisterFormModel.fromDocument();

  if ( form ) {
    form.loadState();

    if (form.fields.optionalPhoneNumber) {
      initPhoneField(form, form.fields.optionalCountryCode, form.fields.optionalCountryIsoName, form.fields.optionalPhoneNumber);
    }
  }

  return form;
}

function checkForCredentials() {
  if (navigator.credentials) {
    navigator.credentials.get({
      password: true,
    })
      .then(c => {
        if (c instanceof PasswordCredential) {
          var link = getElementById("sign_in_page_link");
          window.location.replace(link.elem.href);
        }
      });
  }
}

