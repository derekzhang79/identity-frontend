const className = 'two-step-signin';
const slideClassName = 'two-step-signin__slide';

const SLIDE_STATE_READY = 'SLIDE_STATE_READY';
const SLIDE_STATE_LOADING = 'SLIDE_STATE_LOADING';

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

  $form.addEventListener('submit', (ev) => {
    ev.preventDefault();
    $form.dataset.state = SLIDE_STATE_LOADING;
    fetch('/signin/new',{
      credentials: 'include',
    })
      .then(response => response.text())
      .then(text => {
        const $new = getSlideFromFetch(text);
        pushSlide($slide, $new);
      })
  })

}

export {init, className}
