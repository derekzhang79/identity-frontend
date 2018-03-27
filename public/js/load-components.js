/* @flow */

import Raven from 'raven-js';

import {
  init as initTwoStepSignin,
  selector as selectorTwoStepSignin,
  initOnce as initOnceTwoStepSignin
} from 'components/two-step-signin/two-step-signin';
import {
  init as initTwoStepSigninSlide,
  selector as selectorTwoStepSigninSlide
} from 'components/two-step-signin/two-step-signin__slide';
import {
  init as initSmartLock,
  selector as selectorSmartLock
} from 'components/smartlock-trigger/smartlock-trigger';
import {
  init as initTwoStepSignInGa,
  selector as selectorTwoStepSignInGa
} from 'components/two-step-signin/two-step-signin__ga-client-id';
import {
  init as initFormInput,
  selector as selectorFormInput
} from 'components/form/form-input';
import {
  init as initInPageClick,
  selector as selectorInPageClick
} from 'components/analytics/analytics-in-page-click';

const ERR_MALFORMED_LOADER = 'Missing loader parts';
const ERR_COMPONENT_THROW = 'Uncaught component error';

const components: any[] = [
  [initFormInput, selectorFormInput],
  [initTwoStepSignin, selectorTwoStepSignin, initOnceTwoStepSignin],
  [initTwoStepSigninSlide, selectorTwoStepSigninSlide],
  [initSmartLock, selectorSmartLock],
  [initInPageClick, selectorInPageClick],
  [initTwoStepSignInGa, selectorTwoStepSignInGa]
];

const initOnceList = [];

class Component {
  init: HTMLElement => Promise<void>;
  initOnce: ?() => Promise<void>;
  selector: string;

  constructor(componentArr: any[]) {
    if (typeof componentArr[0] !== 'function') {
      throw new Error([ERR_MALFORMED_LOADER, componentArr]);
    }
    if (typeof componentArr[1] !== 'string') {
      throw new Error([ERR_MALFORMED_LOADER, componentArr]);
    }
    this.init = componentArr[0];
    this.selector = componentArr[1];
    if (componentArr[2] && typeof componentArr[2] === 'function') {
      this.initOnce = componentArr[2];
    }
  }
}

const loadComponent = ($root: HTMLElement, component: Component): void => {
  try {
    [...$root.querySelectorAll(component.selector)].forEach($target => {
      if (component.initOnce && !initOnceList.includes(component.selector)) {
        component.initOnce();
        initOnceList.push(component.selector);
      }
      $target.dataset.enhanced = 'true';
      component.init($target);
    });
  } catch (err) {
    Raven.captureException(err, [ERR_COMPONENT_THROW, component.selector]);
    console.error(err, [ERR_COMPONENT_THROW, component.selector]);
  }
};

const loadComponents = ($root: HTMLElement): void => {
  components.forEach(component => {
    loadComponent($root, new Component(component));
  });
};

export { loadComponents };
export default loadComponents;
