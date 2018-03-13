// @flow

const $elements: HTMLElement[] = [];

const className: string = 'form-error-wrap';

const showError = (error: string) => {
  if ($elements.length < 1) {
    alert(error); /* eslint-disable-line no-alert */
  } else {
    const $div = document.createElement('div');
    $div.innerText = error;
    $elements.forEach($element => {
      const childClassName = $element.dataset.appendClassname;
      if (childClassName) $div.className = childClassName;
      $element.appendChild($div.cloneNode(true));
    });
  }
};

const init = ($element: HTMLElement): Promise<void> => {
  $elements.push($element);
  return Promise.resolve();
};

export { className, init, showError };
