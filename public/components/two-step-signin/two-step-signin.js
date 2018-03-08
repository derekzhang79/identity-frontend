import {EV_DONE} from 'components/two-step-signin/two-step-signin-form';

const className = 'two-step-signin';
const slideClassName = 'two-step-signin__slide';

const ERR_MALFORMED_FETCH = 'Something went wrong';

const getSlideFromFetch = (textHtml) => {
  const $wrapper = document.createElement('div');
  $wrapper.innerHTML = textHtml;

  const $form = $wrapper.querySelector(`.${slideClassName}`);
  if($form !== null) return $form;
  else throw new Error(ERR_MALFORMED_FETCH);
}

const pushSlide = ($old, $new) => {
  $old.addEventListener('animationend', () => {
    $old.remove();
  });
  requestAnimationFrame(()=>{
    $old.classList.remove('two-step-signin__slide--visible');
    $old.classList.add('two-step-signin__slide--out');
  })
  $new.addEventListener('animationend', () => {
    [
      'two-step-signin__slide--in',
      'two-step-signin__slide--out',
      'two-step-signin__slide--in-reverse',
      'two-step-signin__slide--out-reverse'
    ].forEach(_ => $new.classList.remove(_));
  });
  requestAnimationFrame(()=> {
    ['two-step-signin__slide--visible', 'two-step-signin__slide--in'].forEach(_ => $new.classList.add(_))
  });
  $old.parentNode.appendChild($new);
  return Promise.resolve($new);
}

const init = ($component) => {

  const $slide = $component.querySelector(`.${slideClassName}`)
  const $form = $component.querySelector('form');

  $form.addEventListener(EV_DONE, (ev) => {
    history.pushState({},'',ev.detail.url);
    const $new = getSlideFromFetch(ev.detail.request);
    pushSlide($slide, $new);
  })

}

export {init, className}
