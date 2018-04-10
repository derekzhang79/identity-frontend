// @flow

const selector: string = '.oauth-email';

const init = ($component: HTMLElement): void => {

  [...element.childNodes].forEach(node => {
    debugger;
    node.style.display = 'none';
  })
};

export { init, selector };
