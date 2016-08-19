export function initPhoneField (form, domElem) {

  const PLUGIN_OPTIONS = {
    nationalMode: false,
    initialCountry: 'gb',
    preferredCountries: ['gb', 'us', 'au']
  };

  require( [ 'jquery', 'intl-tel-input', 'intl-tel-input/build/js/utils.js' ], ($) => {

    const tooltipElement = form.formElement.elem.querySelector('.register-form__tooltip--phone-number');
    $(domElem.elem).intlTelInput(PLUGIN_OPTIONS);
    attachListeners(form.formElement.elem);

    function attachListeners (formElem) {
      formElem.addEventListener('click', setPhoneNumber);
      formElem.querySelector('.register-form__link--why-phone-number').addEventListener('click', toggleDropdown);
      formElem.querySelector('.register-form__tooltip--phone-number__close').addEventListener('click', toggleDropdown);
    }

    function setPhoneNumber () {
      domElem.setValue($(domElem.elem).intlTelInput('getNumber'));
    }

    function toggleDropdown () {
      if (tooltipElement.hasAttribute('hidden')) {
        tooltipElement.removeAttribute('hidden');
      } else {
        tooltipElement.setAttribute('hidden', '');
      }
    }
  });
}
