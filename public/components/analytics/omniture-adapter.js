/**
 * Adapter for analytics with Omniture.
 *
 * Adapted from Frontend at: https://github.com/guardian/frontend/blob/master/static/src/javascripts/projects/common/modules/analytics/omniture.js
 */

import s from './omniture';

import { getMvtFullId, getActiveTestsAndResultsForOmniture } from './mvt';

function Omniture() {
    this.s = s;
}

Omniture.prototype.logView = function () {
    this.s.t();
    this.confirmPageView();
};

Omniture.prototype.generateTrackingImageString = function () {
    return 's_i_' + s.account.split(',').join('_');
};


Omniture.prototype.populatePageProperties = function () {
    var now      = new Date(),
        tpA      = this.s.getTimeParting('n', '+0'),
        /* Retrieve navigation interaction data */
        platform = 'frontend',
        mvtTag      = getActiveTestsAndResultsForOmniture(),
        // Tag the identity of this user, which is composed of
        // the omniture visitor id, the ophan browser id, and the frontend-only mvt id.
        mvtId    = getMvtFullId();

    // http://www.scribd.com/doc/42029685/15/cookieDomainPeriods
    this.s.cookieDomainPeriods = '2';

    this.s.linkInternalFilters += ',localhost,gucode.co.uk,gucode.com,guardiannews.com,int.gnl,proxylocal.com,theguardian.com';

    this.s.trackingServer = 'hits.theguardian.com';
    this.s.trackingServerSecure = 'hits-secure.theguardian.com';

    this.s.ce = 'UTF-8';
    this.s.pageName  = 'signin'; // TODO: config.page.analyticsName;

    // eVar1 contains today's date
    // in the Omniture backend it only ever holds the first
    // value a user gets, so in effect it is the first time
    // we saw this user
    this.s.eVar1 = now.getFullYear() + '/' +
        pad(now.getMonth() + 1, 2) + '/' +
        pad(now.getDate(), 2);

    // TODO user id
    //if (id.getUserFromCookie()) {
    //    this.s.prop2 = 'GUID:' + id.getUserFromCookie().id;
    //    this.s.eVar2 = 'GUID:' + id.getUserFromCookie().id;
    //}

    this.s.channel   = 'identity';

    // getting clientWidth causes a reflow, so avoid using if possible
    this.s.eVar21    = (window.innerWidth || document.documentElement.clientWidth)
                + 'x'
                + (window.innerHeight || document.documentElement.clientHeight);

    /* Set Time Parting Day and Hour Combination - 0 = GMT */
    this.s.prop20    = tpA[2] + ':' + tpA[1];
    this.s.eVar20    = 'D=c20';

    this.s.prop19    = platform;

    //this.s.prop31    = id.getUserFromCookie() ? 'registered user' : 'guest user';
    //this.s.eVar31    = id.getUserFromCookie() ? 'registered user' : 'guest user';

    this.s.eVar51  = mvtTag;

    this.s.list1  = mvtTag;

    if (this.s.eVar51) {
        this.s.events = this.s.apl(this.s.events, 'event58', ',');
    }

    if (mvtId) {
        this.s.eVar60 = mvtId;
    }

    // Identity specific config
    this.s.prop11 = 'Users';
    this.s.prop9 = 'userid';
    //this.s.eVar27 = config.page.omnitureErrorMessage || '';
    this.s.eVar42 = ''; //config.page.returnUrl || '';
    this.s.hier2 = 'GU/Users/Registration';

    this.s.prop56    = 'Javascript';

    /* Omniture library version */
    this.s.prop62    = 'Guardian JS-1.4.1 20140914';

    // Set Page View Event
    this.s.events    = this.s.apl(this.s.events, 'event4', ',', 2);

    this.s.prop67    = 'nextgen-served';

    this.s.prop30 = 'non-content';


    if (this.s.getParamValue('INTCMP') !== '') {
        this.s.eVar50 = this.s.getParamValue('INTCMP');
    }
    this.s.eVar50 = this.s.getValOnce(this.s.eVar50, 's_intcampaign', 0);

    // the operating system
    this.s.eVar58 = navigator.platform || 'unknown';
};

Omniture.prototype.go = function () {
    this.populatePageProperties();
    this.logView();
};

Omniture.prototype.confirmPageView = function () {
    // This ensures that the Omniture pageview beacon has successfully loaded
    // Can be used as a way to prevent other events to fire earlier than the pageview
    var checkForPageViewInterval = setInterval(function () {
        // s_i_guardiangu-frontend_guardiangu-network is a globally defined Image() object created by Omniture
        // It does not sit in the DOM tree, and seems to be the only surefire way
        // to check if the intial beacon has been successfully sent
        var img = window[this.generateTrackingImageString()];
        if (typeof (img) !== 'undefined' && (img.complete === true || img.width + img.height > 0)) {
            clearInterval(checkForPageViewInterval);
        }
    }.bind(this), 250);

    // Give up after 10 seconds
    setTimeout(function () {
        clearInterval(checkForPageViewInterval);
    }, 10000);
};

// A single Omniture instance for the whole application.
export default new Omniture();


/** migrated functions **/

function pad(number, length) {
  var str = '' + number;
  while (str.length < length) {
    str = '0' + str;
  }
  return str;
}

