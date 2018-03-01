import { addError as addInputError } from './sts-input';
import { bindSubmit, setSlideState, SLIDE_STATE_READY} from "./sts-slider";

const submitEmail = (email) => {
  return new Promise(yay => {
    setTimeout(()=>yay(),500+(Math.random()*1000));
  })
}

export const showSlide = ($slide, onSubmit) => {
  const $passwordField = $slide.querySelector('input[name="password"]');
  const $emailField = $slide.querySelector('input[name="email"]');
  setTimeout(()=>{
    $emailField.focus();
  },500)
  bindSubmit($slide, () => {
    if($emailField.value.trim().length < 1) {
      addInputError($emailField, 'Invalid email');
      return Promise.reject();
    }
    return submitEmail($emailField.value).then(()=>{
      setSlideState($slide, SLIDE_STATE_READY);
      return onSubmit();
    })
  })
};
