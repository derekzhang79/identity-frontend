import { init as initTwoStepSignin , className as classNameTwoStepSignin } from 'components/two-step-signin/two-step-signin.js';
import { init as initTwoStepSigninForm, className as classNameTwoStepSigninForm } from 'components/two-step-signin/two-step-signin-form.js';

const components = [
  [initTwoStepSignin, classNameTwoStepSignin],
  [initTwoStepSigninForm, classNameTwoStepSigninForm],
]

const loadComponents = ($root) => {
  components.forEach(component => {
    [...$root.querySelectorAll(`.${component[1]}`)]
      .filter($target => !$target.dataset.enhanced)
      .forEach($target=>{
        $target.dataset.enhanced = true;
        component[0]($target);
      })
  })
}

export {loadComponents}
export default loadComponents;
