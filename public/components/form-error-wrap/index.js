// @flow

const $elements: HTMLElement[] = [];

const className: string = 'form-error-wrap';

const errors = [];

const renderErrors = () => {
  const lightModeUrl = `${document.location.href}?&no-js`;
  const retryError = `If the problem persists, try <a href="${lightModeUrl}">light mode</a>.`;

  [...errors, retryError].forEach(error => {
    const $div = document.createElement('div');
    $div.innerHTML = error;
    $elements.forEach($element => {
      const childClassName = $element.dataset.appendClassname;
      if (childClassName) $div.className = childClassName;
      $element.appendChild($div.cloneNode(true));
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
