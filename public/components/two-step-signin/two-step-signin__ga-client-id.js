// @flow

import { fetchTracker } from '../analytics/ga';

const selector: string = '.two-step-signin__ga-client-id';

const init = ($component: HTMLInputElement): void => {
  fetchTracker(tracker => {
    $component.value = tracker.get('clientId');
  });
};

export { init, selector };
