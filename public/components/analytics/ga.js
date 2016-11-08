/**
 * GA Tracking
 */
import { configuration } from '../configuration/configuration';

export function init() {
  const gaUID = configuration.gaUID;
  return record(gaUID);
}

function record(gaUID) {
  loadGA();
  ga('create', gaUID, 'auto');
  ga('send', 'pageview');
}

function loadGA() {
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
      (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
    m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');
}
