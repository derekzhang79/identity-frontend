// @flow

import {customMetric} from "./ga";

const selector: string = '*[data-link-name]';

const init = ($component: HTMLElement): void => {
  if($component.dataset.linkName) {
    $component.addEventListener('click', () => {
      customMetric({name: $component.dataset.linkName, type: 'inPageClick'});
    })
  }
};

export { init, selector };

