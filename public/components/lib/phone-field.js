const PLUGIN_OPTIONS = {
  initialCountry: 'gb',
  preferredCountries: ['gb', 'us', 'au']
};

export function initPhoneField (form, countryCodeElement, localNumberElement) {

  require( [ 'jquery', 'intl-tel', 'intl-tel-utils' ], ($) => {

    const tooltipElement = form.formElement.elem.querySelector('.register-form__tooltip--phone-number');
    const formElement = form.formElement.elem;
    initializeFields(
      $(formElement),
      $(countryCodeElement.elem),
      $(localNumberElement.elem)
    );
    $('.register-form__link--why-phone-number', formElement).on('click', toggleDropdown);
    $('.register-form__tooltip--phone-number__close', formElement).on('click', toggleDropdown);

    function toggleDropdown () {
      if (tooltipElement.hasAttribute('hidden')) {
        tooltipElement.removeAttribute('hidden');
      } else {
        tooltipElement.setAttribute('hidden', '');
      }
    }
  });
}

function initializeFields (form, countryCode, localNumber) {
  // The core view has a select and an input field. When JS is enabled and running
  // hide the select and replace it with a jQuery phone number plugin
  localNumber.intlTelInput(PLUGIN_OPTIONS);
  countryCode.parent().hide();
  localNumber.parents('.register-form__control-column--local-number')
    .removeClass('register-form__control-column--local-number')
    .addClass('register-form__control-column--local-number--wide')

  form.on('submit', updateHiddenField);

  function updateHiddenField () {
    const dialCode = localNumber.intlTelInput('getSelectedCountryData').dialCode;
    countryCode.val(dialCode);
    localNumber.val(localNumber.intlTelInput('getNumber').replace(new RegExp('^\\+' + dialCode), ''));
  }
}
