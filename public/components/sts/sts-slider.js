export const SLIDE_STATE_READY = 'SLIDE_STATE_READY';
export const SLIDE_STATE_LOADING = 'SLIDE_STATE_LOADING';

export const bindSubmit = ($slide, handler) => {
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

export const setSlideState = ($slide, state) => {
  $slide.dataset.state = state;
}
