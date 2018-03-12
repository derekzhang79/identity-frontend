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

const components = [
  [initTwoStepSignin, classNameTwoStepSignin, initOnceTwoStepSignin],
  [initTwoStepSigninSlide, classNameTwoStepSigninSlide],
  [initSmartLock, classNameSmartLock]
];

const initOnceList = [];

const loadComponents = $root => {
  components.forEach(component => {
    [...$root.querySelectorAll(`.${component[1]}`)]
      .filter($target => !$target.dataset.enhanced)
      .forEach($target => {
        if (component[2] && !initOnceList.includes(component[1])) {
          component[2]();
          initOnceList.push(component[1]);
        }
        $target.dataset.enhanced = true;
        component[0]($target);
      });
  });
};

export { loadComponents };
export default loadComponents;
