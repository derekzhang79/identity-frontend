import { init as initOphan } from "./ophan";
import { init as initGA } from "./ga";

// Log initial page view events
export function logPageView() {
  // for ophan, this will also log the initial page view:
  initOphan();
  initGA();
}
