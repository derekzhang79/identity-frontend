// @flow

import { route } from 'js/config';
import { showError } from '../form-error-wrap/index';

const className: string = 'two-step-signin__slide';

const SLIDE_STATE_LOADING: string = 'SLIDE_STATE_LOADING';
const SLIDE_STATE_DEFAULT: string = 'SLIDE_STATE_DEFAULT';

const EV_DONE: string = 'form-done';

const ERR_MALFORMED_HTML: string = 'Something went wrong';
const ERR_MALFORMED_RESPONSE: string = 'Something went wrong';

const initStepOneForm = (
  $component: HTMLFormElement,
  $parent: HTMLElement
): void => {
  if (!$component || !$parent) {
    throw new Error(ERR_MALFORMED_HTML);
  }

  $component.addEventListener('submit', (ev: Event) => {
    ev.preventDefault();
    $parent.dataset.state = SLIDE_STATE_LOADING;

    fetch($component.action, {
      credentials: 'include',
      method: 'POST',
      body: new FormData($component)
    })
      .then(response => {
        if (response.code !== 200) {
          throw new Error([ERR_MALFORMED_RESPONSE, response]);
        }
        return Promise.all([response.text(), response.url]);
      })
      .then(([text, url]) => {
        const event = new CustomEvent(EV_DONE, {
          bubbles: true,
          detail: {
            responseHtml: text,
            url
          }
        });
        $parent.dispatchEvent(event);
      })
      .catch(() => {
        showError('Something went wrong! Try again');
        $parent.dataset.state = SLIDE_STATE_DEFAULT;
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

  if (new URL($form.action).pathname === '/actions/signin/with-email') {
    initStepOneForm($form, $component);
  }

  $resetLinks.forEach(($resetLink: HTMLElement) => {
    $resetLink.addEventListener('click', (ev: Event) => {
      ev.preventDefault();

      fetch(route('twoStepSignIn'), {
        credentials: 'include'
      })
        .then(response => Promise.all([response.text(), response.url]))
        .then(([text, url]) =>
          $component.dispatchEvent(
            new CustomEvent(EV_DONE, {
              bubbles: true,
              detail: {
                responseHtml: text,
                url,
                reverse: true
              }
            })
          )
        );
    });
  });
};

export { init, className, EV_DONE };
