import { fetchTracker } from '../analytics/ga';

export default class OAuthCtaModel {
  constructor(oAuthCtaAnchors) {
    this.oAuthCtaAnchors = oAuthCtaAnchors;
    this.saveClientId();
  }

  saveClientId() {
    fetchTracker(tracker => {
      // Save the GA client id to be passed with the form submission
      const clientId = encodeURI(tracker.get('clientId'));

      for (let elem of this.oAuthCtaAnchors) {
        const connector = elem.search.length ? '&' : '?';
        elem.setAttribute(
          'href',
          `${elem.getAttribute('href')}${connector}gaClientId=${clientId}`
        );
      }
    });
  }

  static fromDocument() {
    const oAuthCtaAnchors = document.getElementsByClassName(
      'oauth__cta--anchor'
    );

    if (oAuthCtaAnchors) {
      return new OAuthCtaModel(oAuthCtaAnchors);
    }
  }
}

export const init = OAuthCtaModel.fromDocument;
