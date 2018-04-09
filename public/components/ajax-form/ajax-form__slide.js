// @flow

import { route } from 'js/config';
import { showErrorText } from '../form-error-wrap/index';
import { getUrlErrors } from '../../js/get-url-errors';

type ValidRouteList = string[];

const selector: string = '.ajax-form__slide';

const SLIDE_STATE_LOADING: string = 'SLIDE_STATE_LOADING';
const SLIDE_STATE_DEFAULT: string = 'SLIDE_STATE_DEFAULT';

const EV_DONE: string = 'form-done';

const ERR_MALFORMED_HTML: string = 'ERR_MALFORMED_HTML';
const ERR_MALFORMED_RESPONSE: string = 'ERR_MALFORMED_RESPONSE';
const ERR_BACKEND_ERROR: string = 'ERR_BACKEND_ERROR';

const validAjaxFormRoutes: ValidRouteList = [
  route('twoStepSignInAction'),
  route('signInSecondStepCurrentAction')
];

const validAjaxLinkRoutes: ValidRouteList = [route('twoStepSignIn')];

const getSlide = ($component: HTMLElement): HTMLElement => {
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

const fetchSlide = (
  action: string,
  $stateable: HTMLElement,
  fetchProps: {}
): Promise<string[]> =>
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

const catchSlide = ($stateable: HTMLElement, err: Error): void => {
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

const fetchAndDispatchSlide = (
  action: string,
  $stateable: HTMLElement,
  fetchProps: {},
  props: { reverse: boolean } = { reverse: false }
): Promise<void> =>
  fetchSlide(action, $stateable, fetchProps)
    .then(([responseHtml, url]) =>
      dispatchDone($stateable, {
        $slide: getSlideFromFetch(responseHtml),
        url,
        reverse: props.reverse
      })
    )
    .catch(err => catchSlide($stateable, err));

const init = ($component: HTMLElement): void => {
  const $links: HTMLAnchorElement[] = [
    ...($component.querySelectorAll(`a.ajax-form__link`): any)
  ]
    .filter(_ => _ instanceof HTMLAnchorElement)
    .filter(_ =>
      validAjaxLinkRoutes.map(r => _.href.contains(r)).some(c => c === true)
    );

  const $forms: HTMLFormElement[] = [
    ...($component.querySelectorAll(`form`): any)
  ]
    .filter(_ => _ instanceof HTMLFormElement)
    .filter(_ =>
      validAjaxFormRoutes.map(r => _.action.contains(r)).some(c => c === true)
    );

  $forms.forEach(($form: HTMLFormElement) => {
    $form.addEventListener('submit', (ev: Event) => {
      ev.preventDefault();
      fetchAndDispatchSlide($form.action, $component, {
        method: 'post',
        body: new FormData($form)
      });
    });
  });

  $links.forEach(($link: HTMLAnchorElement) => {
    $link.addEventListener('click', (ev: Event) => {
      ev.preventDefault();
      fetchAndDispatchSlide(
        $link.href,
        $component,
        {
          method: 'get'
        },
        {
          reverse: $link.dataset.isReverse !== null
        }
      );
    });
  });
};

export { init, selector, EV_DONE, getSlide, getSlideFromFetch };
