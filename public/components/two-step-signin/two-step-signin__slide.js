// @flow

import { route } from 'js/config';

const className: string = 'two-step-signin__slide';

const EV_DONE: string = 'form-done';

const ERR_MALFORMED_HTML: string = 'Something went wrong';

const initStepOneForm = (
  $component: HTMLFormElement,
  $parent: HTMLElement
): void => {
  if (!$component || !$parent) {
    throw new Error(ERR_MALFORMED_HTML);
  }
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
