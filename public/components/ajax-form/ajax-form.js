// @flow

import {
  EV_DONE,
  getSlide
} from 'components/ajax-form/ajax-form__slide';
import { loadComponents } from 'js/load-components';
import { pageView } from '../analytics/ga';

const selector: string = '.ajax-form';

const ERR_MALFORMED_EVENT: string = 'Something went wrong';
const ERR_MALFORMED_HTML: string = 'Something went wrong';

const STATE_INITIATOR: string = 'ajax-form-state-init';

const pushSlide = (
  $old: HTMLElement,
  $new: HTMLElement,
  reverse: boolean = false
): Promise<HTMLElement> => {
  const classNames = reverse
    ? {
        in: 'ajax-form__slide--in-reverse',
        out: 'ajax-form__slide--out-reverse'
      }
    : {
        in: 'ajax-form__slide--in',
        out: 'ajax-form__slide--out'
      };

  const animateOut = () =>
    new Promise(resolve => {
      if ('AnimationEvent' in window) {
        $old.addEventListener('animationend', () => {
          $old.remove();
          resolve($new);
        });
        requestAnimationFrame(() => {
          $old.classList.remove('ajax-form__slide--visible');
          $old.classList.add(classNames.out);
        });
        $new.addEventListener('animationend', () => {
          [
            'ajax-form__slide--in',
            'ajax-form__slide--out',
            'ajax-form__slide--in-reverse',
            'ajax-form__slide--out-reverse'
          ].forEach(_ => $new.classList.remove(_));
        });
        requestAnimationFrame(() => {
          ['ajax-form__slide--visible', classNames.in].forEach(_ =>
            $new.classList.add(_)
          );
        });
      } else {
        $old.remove();
        resolve($new);
      }
    });

  const $oldParent = $old.parentNode;
  if ($oldParent) {
    $oldParent.appendChild($new);
  } else {
    throw new Error(ERR_MALFORMED_HTML);
  }
  return animateOut();
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

const onSlide = (
  $component: HTMLElement,
  $slide: HTMLElement,
  href: String,
  isInitial: boolean = false
): void => {
  /* push state */
  if (isInitial) {
    window.history.replaceState(
      {
        initiator: STATE_INITIATOR
      },
      '',
      href
    );
  } else {
    window.history.pushState(
      {
        initiator: STATE_INITIATOR
      },
      '',
      href
    );
  }

  /* tell GA about it */
  if (!isInitial) {
    pageView();
  }

  /* make sure the container looks ok during transitions */
  $component.style.minHeight = `${$slide.clientHeight * 1.1}px`;

  /* autofocus inputs after push */
  const $focusable = $slide.querySelector('[autofocus]');
  if ($focusable) {
    $focusable.focus();
  }
};

const preservePasswordField = (
  $oldSlide: HTMLElement,
  $newSlide: HTMLElement
) => {
  const $passwordOld = $oldSlide.querySelector('input[name=password]');
  const $passwordNew = $newSlide.querySelector('input[name=password]');
  if ($passwordOld && $passwordNew && $passwordNew.parentElement) {
    $passwordOld.className = $passwordNew.className;
    $passwordNew.parentElement.replaceChild($passwordOld, $passwordNew);
  }
};

const init = ($component: HTMLElement): void => {
  onSlide($component, getSlide($component), window.location.href, true);
  $component.addEventListener(EV_DONE, (ev: mixed) => {
    if (ev instanceof CustomEvent) {
      const $slide = getSlide($component);
      const $new = ev.detail.$slide;
      const url = ev.detail.url;

      if (!$slide || !$new) {
        throw new Error(ERR_MALFORMED_HTML);
      }

      /* naively attempt to preserve password */
      preservePasswordField($slide, $new);

      /* push the slide */
      pushSlide($slide, $new, ev.detail.reverse).then(() => {
        onSlide($component, $new, url);
        loadComponents((($new.parentElement: any): HTMLElement));
      });
    } else {
      throw new Error(ERR_MALFORMED_EVENT);
    }
  });
};

export { init, selector, initOnce };
