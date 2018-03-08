const className = 'two-step-signin-form';

const SLIDE_STATE_READY = 'SLIDE_STATE_READY';
const SLIDE_STATE_LOADING = 'SLIDE_STATE_LOADING';

const ERR_MALFORMED_FETCH = 'Something went wrong';

const EV_DONE = 'form-done';

const init = ($component) => {

  $component.addEventListener('submit', (ev) => {
    ev.preventDefault();
    $component.dataset.state = SLIDE_STATE_LOADING;

    fetch('/signin/existing',{
      credentials: 'include',
    })
      .then(response =>
        Promise.all([
          response.text(),
          response.url
        ])
      ).then(([text,url]) => {
        const event = new CustomEvent(EV_DONE, {
          detail: {
            request: text,
            url: url,
          }
        });
        $component.dispatchEvent(event);
        $component.dataset.state = SLIDE_STATE_READY;
      })
  })

}

export {init, className, EV_DONE}
