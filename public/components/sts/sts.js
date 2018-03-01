import { addError as addInputError } from './sts-input';

const ERR_MALFORMED_HTML = 'Something went wrong';
const ERR_MALFORMED_RESPONSE = 'Something went wrong';
const ERR_SLIDE_MISSING = `Couldn't find page`;

const SLIDE_STATE_READY = 'SLIDE_STATE_READY';
const SLIDE_STATE_LOADING = 'SLIDE_STATE_LOADING';

const getHelperFields = ($element) => {
  const $helpers = $element.querySelector('form.sts-slider__helpers');
  if(!$helpers) throw new Error(ERR_MALFORMED_HTML)
  return new FormData($helpers);
}

const showSlide = ($slideToShow, $element) => {
  [...$element.querySelectorAll('.sts-slider__slide')].forEach($existingSlide=>{
    $existingSlide.addEventListener('animationend', () => {
      $existingSlide.remove();
    });
    requestAnimationFrame(()=>{
      $existingSlide.classList.remove('sts-slider__slide--visible');
      $existingSlide.classList.add('sts-slider__slide--out');
    })
  })
  const $slide = document.createElement('form');
  $slide.addEventListener('animationend', () => {
    [
      'sts-slider__slide--in',
      'sts-slider__slide--out',
      'sts-slider__slide--in-reverse',
      'sts-slider__slide--out-reverse'
    ].forEach(_ => $slide.classList.remove(_));
  });
  requestAnimationFrame(()=> {
    ['sts-slider__slide--visible', 'sts-slider__slide--in'].forEach(_ => $slide.classList.add(_))
  });
  $slide.classList.add('sts-slider__slide');
  $slide.innerHTML = $slideToShow.innerText;
  $element.appendChild($slide);
  return Promise.resolve($slide);
}

const bindSubmit = ($slide, handler) => {
  $slide.addEventListener('submit', ev => {
    ev.preventDefault();
    setSlideState($slide, SLIDE_STATE_LOADING);
    handler(ev)
      .catch(err=>{
        setSlideState($slide, SLIDE_STATE_READY);
        throw(err);
      })
      .then(()=>{
        setSlideState($slide, SLIDE_STATE_READY);
      })
  })
}

const setSlideState = ($slide, state) => {
  $slide.dataset.state = state;
}

const wire = ($element) => {
  const $slides = [...$element.querySelectorAll('script[type="text/template"]')];

  const getSlide = (name) => {
    const filteredSlides = $slides.filter($slide => $slide.dataset.name === name);
    if(filteredSlides.length < 1) throw new Error(ERR_SLIDE_MISSING)
    if(filteredSlides.length > 1) throw new Error(ERR_MALFORMED_HTML)
    return filteredSlides[0]
  }

  const goToSlide = (name) => {
    return showSlide(getSlide(name), $element).then($slide => {
      [...$slide.querySelectorAll('.sts-js-rewind')].forEach($rewind=>{
        $rewind.addEventListener('click',()=>showRootSlide())
      })
      return $slide;
    })
  }

  const showRootSlide = () => {
    goToSlide('root').then($slide => {
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
          showPasswordSlide($emailField, $passwordField);
        })
      })
    })
  };

  const showPasswordSlide = ($email, $pwd) => {
    goToSlide('password').then($slide => {
      $pwd.classList.remove('u-h');
      $email.classList.add('u-h');
      $slide.querySelector('.sts-slider__slide-password-wrap').appendChild($pwd);
      $slide.appendChild($email);
      setTimeout(()=>{
        $pwd.focus();
      },200)
      const formData = getHelperFields($element);
      bindSubmit($slide, () => {
        const ownFormData = new FormData($slide);
        formData.set('email', ownFormData.get('email'));
        formData.set('password', ownFormData.get('password'));
        return fetch('/actions/signin-ajax', {
          method: 'POST',
          credentials: 'include',
          body: new URLSearchParams([...formData.entries()]),
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded; charset=utf-8'
          }
        })
          .then(response => response.json())
          .then(json => {
            console.log(json);
            if(json['redirect-to']) {
              window.location.href = json['redirect-to']
            }
            else {
              addInputError($pwd, 'Wrong password');
              $pwd.focus();
              document.execCommand("selectall", false, false);
              throw new Error(ERR_MALFORMED_RESPONSE)
            }
          })
      })
    });
  }

  showRootSlide();

}

export const init = () => {
  [...document.querySelectorAll('.sts-slider')].forEach($slider => {
    wire($slider);
  })
}
