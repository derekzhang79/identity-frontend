import {
  EV_DONE,
  EV_START_OVER
} from "components/two-step-signin/two-step-signin__slide";
import { loadComponents } from "js/load-components";
import { route } from "js/config";

const className = "two-step-signin";
const slideClassName = "two-step-signin__slide";

const ERR_MALFORMED_FETCH = "Something went wrong";
const STATE_INITIATOR = "two-step-signin-state-init";

const getSlideFromFetch = textHtml => {
  const $wrapper = document.createElement("div");
  $wrapper.innerHTML = textHtml;

  const $form = $wrapper.querySelector(`.${slideClassName}`);
  if ($form !== null) return $form;
  else throw new Error(ERR_MALFORMED_FETCH);
};

const pushSlide = ($old, $new, reverse = false) => {
  const classNames = reverse
    ? {
        in: "two-step-signin__slide--in-reverse",
        out: "two-step-signin__slide--out-reverse"
      }
    : { in: "two-step-signin__slide--in", out: "two-step-signin__slide--out" };

  return new Promise((resolve, error) => {
    $old.addEventListener("animationend", () => {
      $old.remove();
      resolve($new);
    });
    requestAnimationFrame(() => {
      $old.classList.remove("two-step-signin__slide--visible");
      $old.classList.add(classNames.out);
    });
    $new.addEventListener("animationend", () => {
      [
        "two-step-signin__slide--in",
        "two-step-signin__slide--out",cont
        "two-step-signin__slide--in-reverse",
        "two-step-signin__slide--out-reverse"
      ].forEach(_ => $new.classList.remove(_));
    });
    requestAnimationFrame(() => {
      ["two-step-signin__slide--visible", classNames.in].forEach(_ =>
        $new.classList.add(_)
      );
    });
    $old.parentNode.appendChild($new);
  });
};

const initOnce = () => {
  window.addEventListener("popstate", ev => {
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

const init = $component => {
  $component.addEventListener(EV_DONE, ev => {
    const $slide = $component.querySelector(`.${slideClassName}`);

    history.pushState(
      {
        initiator: STATE_INITIATOR
      },
      "",
      ev.detail.url
    );

    const $new = getSlideFromFetch(ev.detail.responseHtml);
    pushSlide($slide, $new, ev.detail.reverse).then(() => {
      loadComponents($new.parentNode);
    });
  });
};

export { init, className, initOnce };
