// @flow

import { route } from 'js/config';
import { showErrorText } from '../form-error-wrap/index';
import { getUrlErrors } from '../../js/get-url-errors';

const selector: string = '.ajax-form__slide';

const SLIDE_STATE_LOADING: string = 'SLIDE_STATE_LOADING';
const SLIDE_STATE_DEFAULT: string = 'SLIDE_STATE_DEFAULT';

const EV_DONE: string = 'form-done';

const ERR_MALFORMED_HTML: string = 'ERR_MALFORMED_HTML';
const ERR_MALFORMED_RESPONSE: string = 'ERR_MALFORMED_RESPONSE';
const ERR_BACKEND_ERROR: string = 'ERR_BACKEND_ERROR';

const validAjaxFormRoutes = [
  route('twoStepSignInAction'),
  route('signInSecondStepCurrentAction')
];

const validAjaxLinkRoutes = [route('twoStepSignIn')];

const getSlide = ($component: HTMLElement) => {
  const $slide = $component.querySelector(selector);
  if ($slide) return $slide;
  throw new Error([ERR_MALFORMED_HTML, $component]);
};

const getSlideFromFetch = (textHtml: string): HTMLElement => {
  const $wrapper: HTMLElement = document.createElement('div');
  $wrapper.innerHTML = textHtml;

  return getSlide($wrapper);
};

const dispatchDone = (
  $parent,
  {
    $slide,
    url,
    reverse = false
  }: { $slide: HTMLElement, url: string, reverse?: boolean }
): void => {
  const event = new CustomEvent(EV_DONE, {
    bubbles: true,
    detail: {
      $slide,
      url,
      reverse
    }
  });
  $parent.dispatchEvent(event);
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
            headers: {
              'x-gu-browser-rq': 'true'
            },
            redirect: 'follow',
            method: 'POST'
          },
          fetchProps
        )
      )
    )
    .then(response => {
      const errors = getUrlErrors(response.url);
      if (response.status !== 200) {
        throw new Error([ERR_MALFORMED_RESPONSE, response]);
      }
      if (errors.length) {
        throw new Error([ERR_BACKEND_ERROR, ...errors]);
      }
      return response.text().then(text => {
        try {
          const json = JSON.parse(text);
          if (json.returnUrl) {
            window.location.href = json.returnUrl;
            return new Promise(() => {});
          }
          throw new Error([ERR_MALFORMED_RESPONSE, response]);
        } catch (e) {
          return [text, response.url];
        }
      });
    });

const catchSlide = ($stateable, err) => {
  $stateable.dataset.state = SLIDE_STATE_DEFAULT;
  if (err.message.split(',')[0] === ERR_BACKEND_ERROR) {
    err.message
      .split(',')
      .splice(1)
      .forEach(showErrorText);
  } else {
    showErrorText('error-unexpected');
  }
  console.error(err);
};

const initStepOneForm = (
  $component: HTMLFormElement,
  $parent: HTMLElement
): void => {
  if (!$component || !$parent) {
    throw new Error([ERR_MALFORMED_HTML, $component, $parent]);
  }

  $component.addEventListener('submit', (ev: Event) => {
    ev.preventDefault();
    fetchSlide($component.action, $parent, {
      body: new FormData($component)
    })
      .then(([responseHtml, url]) => {
        dispatchDone($parent, { $slide: getSlideFromFetch(responseHtml), url });
      })
      .catch(err => catchSlide($parent, err));
  });
};

const init = ($component: HTMLElement): void => {
  const $form: HTMLFormElement = (($component.querySelector(
    'form'
  ): any): HTMLFormElement);
  const $links: HTMLAnchorElement[] = validAjaxLinkRoutes
    .map(_ => [
      ...($component.querySelectorAll(`a.ajax-form__link[href*="${_}"]`): any)
    ])
    .reduce((acc, _) => [...acc, ..._], [])
    .filter(_ => _ instanceof HTMLAnchorElement);

  if (validAjaxFormRoutes.includes(new URL($form.action).pathname)) {
    initStepOneForm($form, $component);
  }

  $links.forEach(($link: HTMLAnchorElement) => {
    $link.addEventListener('click', (ev: Event) => {
      ev.preventDefault();
      fetchSlide($link.href, $component, {
        method: 'GET'
      })
        .then(([responseHtml, url]) =>
          dispatchDone($component, {
            $slide: getSlideFromFetch(responseHtml),
            url,
            reverse: $link.dataset.isReverse !== null
          })
        )
        .catch(err => catchSlide($component, err));
    });
  });
};

export { init, selector, EV_DONE, getSlide, getSlideFromFetch };
