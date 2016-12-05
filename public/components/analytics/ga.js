/**
 * GA Tracking
 */
import { configuration } from '../configuration/configuration';

const gaTracker = 'IdentityPropertyTracker';
const events = {
  metricMap: {
    'SmartLockSignin': 'metric3',
  }
};

export function init() {
  const gaUID = configuration.gaUID;
  return record(gaUID);
}

export function customMetric(event) {
  ga(gaTracker + '.send', 'event', buildGoogleAnalyticsEvent(event));
}

function record(gaUID) {
  loadGA();
  ga('create', gaUID, 'auto', gaTracker);
  ga(gaTracker + '.send', 'pageview');
}

function buildGoogleAnalyticsEvent(event) {
  const fieldsObject = {
    eventCategory: 'identity',
    eventAction: event.name,
    dimension3: 'profile.theguardian.com',
    dimension4: navigator.userAgent,
    dimension5: window.location.href,
    forceSSL: true
  };

  // Increment the appropriate metric based on the event type
  const metricId = events.metricMap[event.type];
  if (metricId) {
    fieldsObject[metricId] = 1;
  }

  return fieldsObject;
}

function loadGA() {
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
      (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
    m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');
}
