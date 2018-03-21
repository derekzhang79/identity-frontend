// @flow

const displayErrorsClassName: string = 'form-input__display-errors';

const className: string = 'form-input';

const checkAndAdd = ($component: HTMLInputElement): void => {
  if ($component.value.trim().length > 0) {
    $component.classList.add(displayErrorsClassName);
  }
};

const init = ($component: HTMLInputElement): void => {
  checkAndAdd($component);
  $component.addEventListener('focus', () => {
    $component.classList.remove(displayErrorsClassName);
  });
  $component.addEventListener('blur', () => {
    checkAndAdd($component);
  });
};

export { className, init };
