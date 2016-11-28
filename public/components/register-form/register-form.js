
import { getElementById, sessionStorage } from '../browser/browser';

import { mapValues as _mapValues } from '../lib/lodash';

import { initPhoneField } from '../lib/phone-field';

const STORAGE_KEY = 'gu_id_register_state';


class RegisterFormFields {
  constructor(firstNameField, lastNameField, emailField, displayNameField, optionalCountryCode, optionalCountryIsoName, optionalPhoneNumber, optionalHideUsername) {
    this.firstName = firstNameField;
    this.lastName = lastNameField;
    this.email = emailField;
    this.displayName = displayNameField;
    this.optionalCountryCode = optionalCountryCode;
    this.optionalCountryIsoName = optionalCountryIsoName;
    this.optionalPhoneNumber = optionalPhoneNumber;
    this.optionalHideUsername = optionalHideUsername;
  }

  setValues( { firstName, lastName, email, displayName, optionalCountryCode, optionalCountryIsoName, optionalPhoneNumber, optionalHideUsername } = {} ) {
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
    if (this.optionalHideUsername) {
      this.optionalHideUsername.setValue(optionalHideUsername)
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
  constructor( formElement, firstNameField, lastNameField, emailField, displayNameField, optionalCountryCode, optionalCountryIsoName, optionalPhoneNumber, optionalHideUsername ) {
    this.formElement = formElement;

    this.fields = new RegisterFormFields( firstNameField, lastNameField, emailField, displayNameField, optionalCountryCode, optionalCountryIsoName, optionalPhoneNumber, optionalHideUsername );

    this.addBindings();
  }

  addBindings() {
    this.formElement.on( 'submit', this.formSubmitted.bind( this ) );
    this.fields.firstName.on('blur', this.updateUsername.bind( this ));
    this.fields.lastName.on('blur', this.updateUsername.bind( this ));
  }

  updateUsername(){
    if (this.fields.optionalHideUsername && this.fields.optionalHideUsername.value()) {
      const username = this.fields.firstName.value() + this.fields.lastName.value();
      console.log(username);
      this.fields.username.setValue(username);
    }
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
    if (this.fields.optionalHideUsername){
      this.fields.username = this.fields.firstName + this.fields.lastName;
    }
    this.saveState();
  }

  static fromDocument() {
    const form = getElementById( 'register_form' );
    const firstNameField = getElementById( 'register_field_firstname' );
    const lastNameField = getElementById( 'register_field_lastname' );
    const emailField = getElementById( 'register_field_email' );
    const displayNameField = getElementById( 'register_field_displayName' );
    const optionalPhoneNumber = getElementById('register_field_localNumber');
    const optionalCountryCode = getElementById('register_field_countryCode');
    const optionalCountryIsoName = getElementById('register_field_countryIsoName');
    const optionalHideUsername = getElementById('register_field_hideUsername');

    if ( form && firstNameField && lastNameField && emailField && displayNameField ) {
      return new RegisterFormModel( form, firstNameField, lastNameField, emailField, displayNameField, optionalCountryCode, optionalCountryIsoName, optionalPhoneNumber, optionalHideUsername);
    }
  }
}


class RegisterFormState {
  constructor( firstName = "", lastName = "", email = "", displayName = "", optionalCountryCode = "", optionalCountryIsoName = "", optionalPhoneNumber = "", optionalHideUsername = false ) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.displayName = displayName;
    this.optionalCountryCode = optionalCountryCode;
    this.optionalCountryIsoName = optionalCountryIsoName;
    this.optionalPhoneNumber = optionalPhoneNumber;
    this.optionalHideUsername = optionalHideUsername;
  }

  save() {
    sessionStorage.setJSON( STORAGE_KEY, this );
  }

  /**
   * @return {RegisterFormState}
   */
  static fromObject( { firstName, lastName, email, displayName, optionalCountryCode, optionalCountryIsoName, optionalPhoneNumber, optionalHideUsername } = {} ) {
    return new RegisterFormState( firstName, lastName, email, displayName, optionalCountryCode, optionalCountryIsoName, optionalPhoneNumber, optionalHideUsername );
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
          const link = getElementById("sign_in_page_link");
          window.location.replace(link.elem.href);
        }
      });
  }
}

