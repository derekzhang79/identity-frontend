// @flow
import { text } from 'js/config';

const $elements: HTMLElement[] = [];

const className: string = 'form-error-wrap';

const errors: string[] = [];

const renderErrors = (): void => {
  $elements.forEach($element => {
    $element.innerHTML = '';
    errors.forEach(error => {
      const $div = document.createElement('div');
      $div.innerHTML = error;
      const childClassName = $element.dataset.appendClassname;
      if (childClassName) $div.className = childClassName;
      $element.appendChild($div);
    });
  });
};

const showError = (error: string): void => {
  if ($elements.length < 1) {
    alert(error); /* eslint-disable-line no-alert */
  } else {
    errors.push(error);
    renderErrors();
  }
};

const showErrorText = (error: string): void => {
  showError(text(error));
};

const init = ($element: HTMLElement): Promise<void> => {
  $elements.push($element);
  return Promise.resolve();
};

export { className, init, showError, showErrorText };
