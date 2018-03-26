// @flow

import { fetchTracker } from '../analytics/ga';

const className: string = 'oauth-cta';

const init = ($component: HTMLAnchorElement): void => {
  fetchTracker(tracker => {
    const clientId = encodeURI(tracker.get('clientId'));

    const connector = $component.search.length ? '&' : '?';
    $component.href = `${$component.href}${connector}gaClientId=${clientId}`;
  });
};

export { init, className };
