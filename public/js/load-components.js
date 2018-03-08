import { init as initTwoStepSignin, className as initTwoStepSigninClassname } from 'components/two-step-signin/two-step-signin.js';

const components = [
  [initTwoStepSignin,initTwoStepSigninClassname]
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
