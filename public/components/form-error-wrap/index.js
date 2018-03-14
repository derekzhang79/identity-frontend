// @flow

const $elements: HTMLElement[] = [];

const className: string = 'form-error-wrap';

const errors = [];

const renderErrors = () => {
  const lightModeUrl = `${document.location.href}?&no-js`;
  const retryError = `If the problem persists, try <a href="${lightModeUrl}">compatibility mode</a>.`;

  $elements.forEach($element => {
    $element.innerHTML = '';
    [...errors, retryError].forEach(error => {
      const $div = document.createElement('div');
      $div.innerHTML = error;
      const childClassName = $element.dataset.appendClassname;
      if (childClassName) $div.className = childClassName;
      $element.appendChild($div);
    });
  });
};

const showError = (error: string) => {
  if ($elements.length < 1) {
    alert(error); /* eslint-disable-line no-alert */
  } else {
    errors.push(error);
    renderErrors();
  }
};

const init = ($element: HTMLElement): Promise<void> => {
  $elements.push($element);
  return Promise.resolve();
};

export { className, init, showError };
