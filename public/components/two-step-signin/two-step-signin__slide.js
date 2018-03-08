import { route } from "js/config";

const className = "two-step-signin__slide";

const SLIDE_STATE_READY = "SLIDE_STATE_READY";
const SLIDE_STATE_LOADING = "SLIDE_STATE_LOADING";

const EV_DONE = "form-done";

const initStepOneForm = ($component, $parent) => {
  $component.addEventListener("submit", ev => {
    ev.preventDefault();
    $component.dataset.state = SLIDE_STATE_LOADING;

    fetch("/signin/existing", {
      credentials: "include"
    })
      .then(response => Promise.all([response.text(), response.url]))
      .then(([text, url]) => {
        const event = new CustomEvent(EV_DONE, {
          bubbles: true,
          detail: {
            responseHtml: text,
            url: url
          }
        });
        $parent.dispatchEvent(event);
      });
  });
};

const init = $component => {

  const $form = $component.querySelector("form");
  const $resetLinks = [
    ...$component.querySelectorAll(`a[href*="${route("twoStepSignIn")}"]`)
  ];

  if (new URL($form.action).pathname === "/actions/signin/with-email") {
    initStepOneForm($form, $component);
  }

  $resetLinks.forEach($resetLink => {
    $resetLink.addEventListener("click", ev => {
      ev.preventDefault();

      fetch(route("twoStepSignIn"), {
        credentials: "include"
      })
        .then(response => Promise.all([response.text(), response.url]))
        .then(([text, url]) =>
          $component.dispatchEvent(
            new CustomEvent(EV_DONE, {
              bubbles: true,
              detail: {
                responseHtml: text,
                url: url,
                reverse: true
              }
            })
          )
        );
    });
  });
};

export { init, className, EV_DONE };
