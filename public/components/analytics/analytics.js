import omniture from './omniture-adapter';
import { init as initOphan } from './ophan';

// Log initial page view events
export function logPageView() {
  omniture.go();

  // for ophan, this will also log the initial page view:
  initOphan();
}


