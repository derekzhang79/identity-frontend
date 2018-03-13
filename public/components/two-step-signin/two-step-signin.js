// @flow

import { EV_DONE } from 'components/two-step-signin/two-step-signin__slide';
import { loadComponents } from 'js/load-components';

const className: string = 'two-step-signin';
const slideClassName: string = 'two-step-signin__slide';

const ERR_MALFORMED_FETCH: string = 'Something went wrong';
const ERR_MALFORMED_EVENT: string = 'Something went wrong';
const ERR_MALFORMED_HTML: string = 'Something went wrong';

const STATE_INITIATOR: string = 'two-step-signin-state-init';

const getSlideFromFetch = (textHtml: string): HTMLElement => {
  const $wrapper: HTMLElement = document.createElement('div');
  $wrapper.innerHTML = textHtml;

  const $form = $wrapper.querySelector(`.${slideClassName}`);
  if ($form !== null) return $form;
  throw new Error(ERR_MALFORMED_FETCH);
};

const pushSlide = (
  $old: HTMLElement,
  $new: HTMLElement,
  reverse: boolean = false
): Promise<HTMLElement> => {
  const classNames = reverse
    ? {
        in: 'two-step-signin__slide--in-reverse',
        out: 'two-step-signin__slide--out-reverse'
      }
    : { in: 'two-step-signin__slide--in', out: 'two-step-signin__slide--out' };

  return new Promise(resolve => {
    $old.addEventListener('animationend', () => {
      $old.remove();
      resolve($new);
    });
    requestAnimationFrame(() => {
      $old.classList.remove('two-step-signin__slide--visible');
      $old.classList.add(classNames.out);
    });
    $new.addEventListener('animationend', () => {
      [
        'two-step-signin__slide--in',
        'two-step-signin__slide--out',
        'two-step-signin__slide--in-reverse',
        'two-step-signin__slide--out-reverse'
      ].forEach(_ => $new.classList.remove(_));
    });
    requestAnimationFrame(() => {
      ['two-step-signin__slide--visible', classNames.in].forEach(_ =>
        $new.classList.add(_)
      );
    });

    const $oldParent = $old.parentNode;
    if ($oldParent) {
      $oldParent.appendChild($new);
    } else {
      throw new Error(ERR_MALFORMED_HTML);
    }
  });
};

const initOnce = (): void => {
  window.addEventListener('popstate', ev => {
    if (
      ev.state &&
      ev.state.initiator &&
      ev.state.initiator === STATE_INITIATOR
    ) {
      ev.preventDefault();
      ev.stopPropagation();
      window.location.reload();
    }
  });
};

const onSlide = ($component: HTMLElement, $slide: HTMLElement, href: String, isInitial: Boolean = false): void => {
  $component.style.minHeight = `${$slide.clientHeight * 1.1}px`;
  if(isInitial) {
    window.history.replaceState(
      {
        initiator: STATE_INITIATOR
      },
      '',
      href
    )
  }
  else {
    window.history.pushState(
      {
        initiator: STATE_INITIATOR
      },
      '',
      href
    );
  }
};

const getSlide = ($component: HTMLElement) => {
  const $slide = $component.querySelector(`.${slideClassName}`);
  if ($slide) return $slide;
  throw new Error(ERR_MALFORMED_HTML);
};

const init = ($component: HTMLElement): void => {

  onSlide($component, getSlide($component), window.location.href, true);

  $component.addEventListener(EV_DONE, (ev: mixed) => {
    if (ev instanceof CustomEvent) {
      const $slide = getSlide($component);
      const $new = getSlideFromFetch(ev.detail.responseHtml);

      if (!$slide || !$new) {
        throw new Error(ERR_MALFORMED_HTML);
      }
      pushSlide($slide, $new, ev.detail.reverse).then(() => {
        onSlide($component, $new, ev.detail.url);
        loadComponents((($new.parentElement: any): HTMLElement));
      });
    } else {
      throw new Error(ERR_MALFORMED_EVENT);
    }
  });
};

export { init, className, initOnce };
