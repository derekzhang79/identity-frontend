// @flow

import { route } from 'js/config';

const className: string = 'two-step-signin__slide';

const SLIDE_STATE_LOADING: string = 'SLIDE_STATE_LOADING';
const SLIDE_STATE_DEFAULT: string = 'SLIDE_STATE_DEFAULT';

const EV_DONE: string = 'form-done';

const ERR_MALFORMED_HTML: string = 'Something went wrong';
const ERR_MALFORMED_RESPONSE: string = 'Something went wrong';

const validAjaxFormRoutes = [route('twoStepSignInAction')];

const dispatchDone = (
  $parent,
  {
    responseHtml,
    url,
    reverse = false
  }: { responseHtml: string, url: string, reverse?: boolean }
): boolean => {
  const event = new CustomEvent(EV_DONE, {
    bubbles: true,
    detail: {
      responseHtml,
      url,
      reverse
    }
  });
  return $parent.dispatchEvent(event);
};

const fetchSlide = (action, $stateable, fetchProps) =>
  Promise.resolve()
    .then(() => {
      $stateable.dataset.state = SLIDE_STATE_LOADING;
    })
    .then(() =>
      window.fetch(
        action,
        Object.assign(
          {},
          {
            credentials: 'include',
            method: 'POST'
          },
          fetchProps
        )
      )
    )
    .then(response => {
      if (response.status !== 200) {
        throw new Error([ERR_MALFORMED_RESPONSE, response]);
      }
      return Promise.all([response.text(), response.url]);
    })
    .catch(err => {
      $stateable.dataset.state = SLIDE_STATE_DEFAULT;
      throw err;
    });

const initStepOneForm = (
  $component: HTMLFormElement,
  $parent: HTMLElement
): void => {
  if (!$component || !$parent) {
    throw new Error([ERR_MALFORMED_HTML, $component, $parent]);
  }

  $component.addEventListener('submit', (ev: Event) => {
    ev.preventDefault();
    $parent.dataset.state = SLIDE_STATE_LOADING;

    fetchSlide($component.action, $parent, {
      body: new FormData($component)
    })
      .then(([responseHtml, url]) => {
        dispatchDone($parent, { responseHtml, url });
      })
      .catch(() => {
        console.error('errors.generic');
      });
  });
};

const init = ($component: HTMLElement): void => {
  const $form: HTMLFormElement = (($component.querySelector(
    'form'
  ): any): HTMLFormElement);
  const $resetLinks: HTMLElement[] = [
    ...$component.querySelectorAll(`a[href*="${route('twoStepSignIn')}"]`)
  ];

  if (validAjaxFormRoutes.includes(new URL($form.action).pathname)) {
    initStepOneForm($form, $component);
  }

  $resetLinks.forEach(($resetLink: HTMLElement) => {
    $resetLink.addEventListener('click', (ev: Event) => {
      ev.preventDefault();
      fetchSlide(route('twoStepSignIn'), $component, {
        method: 'GET'
      }).then(([responseHtml, url]) =>
        dispatchDone($component, { responseHtml, url, reverse: true })
      );
    });
  });
};

export { init, className, EV_DONE };
