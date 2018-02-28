const ERR_MALFORMED_HTML = 'Something went wrong';
const ERR_SLIDE_MISSING = `Couldn't find page`;

const SLIDE_STATE_READY = 'SLIDE_STATE_READY';
const SLIDE_STATE_LOADING = 'SLIDE_STATE_LOADING';

const submitEmail = (email) => {
  return new Promise(yay => {
    setTimeout(yay(),2500+(Math.random()*1000));
  })
}

const getHelperFields = ($element) => {
  const $helpers = $element.querySelector('form.sts-slider__helpers');
  if(!$helpers) throw new Error(ERR_MALFORMED_HTML)
  return new FormData($helpers);
}

const wire = ($element) => {
  const $slides = [...$element.querySelectorAll('.sts-slider__slide')];

  const showSlide = ($slideToShow) => {
    $slides.forEach($slide => {
      if($slide.classList.contains('sts-slider__slide--visible')) {
        const afterAnimation = () => {
          requestAnimationFrame(()=> {
            debugger;
            ['sts-slider__slide--out'].forEach(_ => $slide.classList.remove(_));
          });
          $slide.removeEventListener('animationend',afterAnimation)
        };
        $slide.addEventListener('animationend', afterAnimation);
        requestAnimationFrame(()=>{
          $slide.classList.remove('sts-slider__slide--visible');
          $slide.classList.add('sts-slider__slide--out');
        })
      }
      else if ($slide.dataset.name === $slideToShow.dataset.name) {
        const afterAnimation = () => {
          requestAnimationFrame(()=> {
            ['sts-slider__slide--in'].forEach(_ => $slide.classList.remove(_));
          });
          $slide.removeEventListener('animationend',afterAnimation)
        };
        $slide.addEventListener('animationend', afterAnimation);
        requestAnimationFrame(()=> {
          ['sts-slider__slide--visible', 'sts-slider__slide--in'].forEach(_ => $slide.classList.add(_))
        });
      }
      else {
        $slide.classList.remove('sts-slider__slide--visible')
      }
    });
  }

  const getSlide = (name) => {
    const filteredSlides = $slides.filter($slide => $slide.dataset.name === name);
    if(filteredSlides.length < 1) throw new Error(ERR_SLIDE_MISSING)
    if(filteredSlides.length > 1) throw new Error(ERR_MALFORMED_HTML)
    return filteredSlides[0]
  }

  const setSlideState = ($slide, state) => {
    $slide.dataset.state = state;
  }

  const $rootSlide = getSlide('root');
  const $pwdSlide = getSlide('password');

  showSlide($rootSlide);

  $rootSlide.addEventListener('submit',ev=>{
    setSlideState($rootSlide, SLIDE_STATE_LOADING);
    submitEmail((new FormData($rootSlide)).get('email')).then(()=>{
      setSlideState($rootSlide, SLIDE_STATE_READY);
      showSlide($pwdSlide);
    })
    ev.preventDefault();
  })

  $pwdSlide.addEventListener('submit',ev=>{
    ev.preventDefault();
    showSlide($rootSlide);
  })

}

export const init = () => {
  [...document.querySelectorAll('.sts-slider')].forEach($slider => {
    wire($slider);
  })
}
