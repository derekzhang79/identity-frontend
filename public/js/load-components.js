/* @flow */

import Raven from 'raven-js';

import {
  init as initTwoStepSignin,
  className as classNameTwoStepSignin,
  initOnce as initOnceTwoStepSignin
} from 'components/two-step-signin/two-step-signin';
import {
  init as initTwoStepSigninSlide,
  className as classNameTwoStepSigninSlide
} from 'components/two-step-signin/two-step-signin__slide';
import {
  init as initSmartLock,
  className as classNameSmartLock
} from 'components/smartlock-trigger/smartlock-trigger';
import {
  init as initFormErrorWrap,
  className as classNameErrorWrap
} from 'components/form-error-wrap/index';

const ERR_MALFORMED_LOADER = 'Missing loader parts';
const ERR_COMPONENT_THROW = 'Uncaught component error';

const components: any[] = [
  [initTwoStepSignin, classNameTwoStepSignin, initOnceTwoStepSignin],
  [initTwoStepSigninSlide, classNameTwoStepSigninSlide],
  [initSmartLock, classNameSmartLock],
  [initFormErrorWrap, classNameErrorWrap]
];

const initOnceList = [];

class Component {
  init: HTMLElement => Promise<void>;
  initOnce: ?() => Promise<void>;
  className: string;

  constructor(componentArr: any[]) {
    if (typeof componentArr[0] !== 'function') {
      throw new Error([ERR_MALFORMED_LOADER, componentArr]);
    }
    if (typeof componentArr[1] !== 'string') {
      throw new Error([ERR_MALFORMED_LOADER, componentArr]);
    }
    this.init = componentArr[0];
    this.className = componentArr[1];
    if (componentArr[2] && typeof componentArr[2] === 'function') {
      this.initOnce = componentArr[2];
    }
  }
}

const loadComponent = ($root: HTMLElement, component: Component): void => {
  try {
    [...$root.querySelectorAll(`.${component.className}`)]
      .filter($target => !$target.dataset.enhanced)
      .forEach($target => {
        if (component.initOnce && !initOnceList.includes(component.className)) {
          component.initOnce();
          initOnceList.push(component.className);
        }
        $target.dataset.enhanced = 'true';
        component.init($target);
      });
  } catch (err) {
    Raven.captureException(err, [ERR_COMPONENT_THROW, component.className]);
    console.error(err, [ERR_COMPONENT_THROW, component.className]);
  }
};

const loadComponents = ($root: HTMLElement): void => {
  components.forEach(component => {
    loadComponent($root, new Component(component));
  });
};

export { loadComponents };
export default loadComponents;
