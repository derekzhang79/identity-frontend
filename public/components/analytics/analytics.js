import omniture from './omniture-adapter';
import { init as initOphan } from './ophan';

// Log page view events
export function logPageView() {
  omniture.go();
  initOphan();
}
