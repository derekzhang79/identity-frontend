export function initPhoneField (form, domElem) {

  const PLUGIN_OPTIONS = {
    nationalMode: false,
    initialCountry: 'gb',
    preferredCountries: ['gb', 'us', 'au']
  };

  require( [ 'jquery', 'intl-tel-input', 'intl-tel-input/build/js/utils.js' ], ($) => {

    $(domElem.elem).intlTelInput(PLUGIN_OPTIONS);

    form.formElement.on('click', function () {
      domElem.setValue($(domElem.elem).intlTelInput('getNumber'));
    });

  });
}
