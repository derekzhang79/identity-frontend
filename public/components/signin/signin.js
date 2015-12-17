/*global window, document*/

export function init() {
  const form = document.getElementById('signin_form');
  const emailField = document.getElementById('signin_field_email');

  const STORAGE_KEY = 'gu_id_state';

  if (!form) { throw new Error('Could not find signin form'); }

  form.addEventListener('submit', () => {
    const email = emailField.value;

    if (email && email.length > 0) {
      window.sessionStorage.setItem(STORAGE_KEY, JSON.stringify({email}));
    }
  });

  if (!emailField.value) {
    const existingState = window.sessionStorage.getItem(STORAGE_KEY);

    if (existingState) {
      const parsedState = JSON.parse(existingState);

      if (parsedState && parsedState.email) {
        emailField.value = parsedState.email;
      }
    }
  }
}
