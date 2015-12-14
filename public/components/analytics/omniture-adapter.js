import s from './omniture';

import { getMvtFullId, getActiveTestsAndResultsForOmniture } from './mvt';

//var R2_STORAGE_KEY = 's_ni', // DO NOT CHANGE THIS, ITS IS SHARED WITH R2. BAD THINGS WILL HAPPEN!
//    NG_STORAGE_KEY = 'gu.analytics.referrerVars';

function Omniture() {
    this.s = s;
    this.pageviewSent = false;

  // TODO do we need link tracking?
}

Omniture.prototype.logView = function () {
    this.s.t();
    this.confirmPageView();
};

Omniture.prototype.generateTrackingImageString = function () {
    return 's_i_' + s.account.split(',').join('_');
};

Omniture.prototype.logTag = function (spec) {
    var storeObj,
        delay;

    if (!spec.validTarget) {
        return;
    }

    if (spec.sameHost && !spec.samePage) {
        // Came from a link to a new page on the same host,
        // so do session storage rather than an omniture track.
        storeObj = {
            pageName: this.s.pageName,
            tag: spec.tag || 'untracked',
            time: new Date().getTime()
        };
        //try { sessionStorage.setItem(R2_STORAGE_KEY, storeObj.tag); } catch (e) {/**/}
        //storage.session.set(NG_STORAGE_KEY, storeObj);
    } else {
        // this is confusing: if s.tl() first param is "true" then it *doesn't* delay.
        delay = spec.samePage ? true : spec.target;
        this.trackLink(delay, spec.tag, { customEventProperties: spec.customEventProperties });
    }
};

Omniture.prototype.populateEventProperties = function (linkName) {

    this.s.linkTrackVars = 'channel,prop1,prop2,prop3,prop4,prop8,prop9,prop10,prop13,prop25,prop31,prop37,prop47,' +
                           'prop51,prop61,prop64,prop65,prop74,eVar7,eVar37,eVar38,eVar39,eVar50,events';
    this.s.linkTrackEvents = 'event37';
    this.s.events = 'event37';
    this.s.eVar37 = (config.page.contentType) ? config.page.contentType + ':' + linkName : linkName;

    // this allows 'live' Omniture tracking of Navigation Interactions
    this.s.eVar7 = 'D=pageName';
    this.s.prop37 = 'D=v37';

    if (/social/.test(linkName)) {
        s.linkTrackVars   += ',eVar12';
        s.linkTrackEvents += ',event16';
        s.events          += ',event16';
        s.eVar12           = linkName;
    }
};

// used where we don't have an element to pass as a tag, eg. keyboard interaction
Omniture.prototype.trackLinkImmediate = function (linkName) {
    // A linkObject of value 'true' means track link with no delay.
    this.trackLink(true, linkName);
};

Omniture.prototype.trackLink = function (linkObject, linkName, options) {
    options = options || {};
    this.populateEventProperties(linkName);
    _.assign(this.s, options.customEventProperties);
    this.s.tl(linkObject, 'o', linkName);
    _.forEach(options.customEventProperties, function (value, key) {
        delete this.s[key];
    });
};

Omniture.prototype.populatePageProperties = function () {
    var now      = new Date(),
        tpA      = this.s.getTimeParting('n', '+0'),
        /* Retrieve navigation interaction data */
        // ni       = '', // TODO storage.session.get(NG_STORAGE_KEY),
        platform = 'frontend',
        mvtTag      = getActiveTestsAndResultsForOmniture(),
        // Tag the identity of this user, which is composed of
        // the omniture visitor id, the ophan browser id, and the frontend-only mvt id.
        mvtId    = getMvtFullId(),
        webPublicationDate = false; //config.page.webPublicationDate;

    // http://www.scribd.com/doc/42029685/15/cookieDomainPeriods
    this.s.cookieDomainPeriods = '2';

    this.s.linkInternalFilters += ',localhost,gucode.co.uk,gucode.com,guardiannews.com,int.gnl,proxylocal.com,theguardian.com';

    this.s.trackingServer = 'hits.theguardian.com';
    this.s.trackingServerSecure = 'hits-secure.theguardian.com';

    this.s.ce = 'UTF-8';
    this.s.pageName  = 'signin'; // TODO: config.page.analyticsName;

    // TODO this.s.prop1     = config.page.headline || '';

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

    //this.s.prop3     = config.page.publication || '';

    this.s.channel   = 'identity';


    // see http://blogs.adobe.com/digitalmarketing/mobile/responsive-web-design-and-web-analytics/
    //this.s.eVar18    = detect.getBreakpoint();

    // getting clientWidth causes a reflow, so avoid using if possible
    this.s.eVar21    = (window.innerWidth || document.documentElement.clientWidth)
                + 'x'
                + (window.innerHeight || document.documentElement.clientHeight);
    //this.s.eVar32    = detect.getOrientation();

    /* Set Time Parting Day and Hour Combination - 0 = GMT */
    this.s.prop20    = tpA[2] + ':' + tpA[1];
    this.s.eVar20    = 'D=c20';

    //this.s.prop60    = detect.isFireFoxOSApp() ? 'firefoxosapp' : null;

    this.s.prop19    = platform;

    //this.s.prop31    = id.getUserFromCookie() ? 'registered user' : 'guest user';
    //this.s.eVar31    = id.getUserFromCookie() ? 'registered user' : 'guest user';

    //this.s.prop40    = detect.adblockInUse || detect.getFirefoxAdblockPlusInstalled();

    //this.s.prop47    = config.page.edition || '';

    this.s.eVar51  = mvtTag;

    this.s.list1  = mvtTag; // allows us to 'unstack' the AB test names (allows longer names)

    // List of components on the page
    //this.s.list2 = _.uniq($('[data-component]')
    //    .map(function (x) { return $(x).attr('data-component'); }))
    //    .toString();
    //this.s.list3 = _.map(history.getPopularFiltered(), function (tagTuple) { return tagTuple[1]; }).join(',');

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
    //this.s.events = this.s.apl(this.s.events, config.page.omnitureEvent, ',');

    this.s.prop56    = 'Javascript';

    /* Omniture library version */
    this.s.prop62    = 'Guardian JS-1.4.1 20140914';

    //this.s.prop63    = detect.getPageSpeed();

    // Set Page View Event
    this.s.events    = this.s.apl(this.s.events, 'event4', ',', 2);

    this.s.prop67    = 'nextgen-served';

    if (webPublicationDate) {
        this.s.prop30 = 'content';
    } else {
        this.s.prop30 = 'non-content';
    }

    if (this.s.getParamValue('INTCMP') !== '') {
        this.s.eVar50 = this.s.getParamValue('INTCMP');
    }
    this.s.eVar50 = this.s.getValOnce(this.s.eVar50, 's_intcampaign', 0);

    // the operating system
    this.s.eVar58 = navigator.platform || 'unknown';


    //if (ni) {
    //    d = new Date().getTime();
    //    if (d - ni.time < 60 * 1000) { // One minute
    //        this.s.eVar24 = ni.pageName;
    //        this.s.eVar37 = ni.tag;
    //        this.s.events = 'event37';
    //
    //        // this allows 'live' Omniture tracking of Navigation Interactions
    //        this.s.eVar7 = ni.pageName;
    //        this.s.prop37 = ni.tag;
    //    }
    //    storage.session.remove(R2_STORAGE_KEY);
    //    storage.session.remove(NG_STORAGE_KEY);
    //}

    //this.s.prop73 = detect.isFacebookApp() ? 'facebook app' : detect.isTwitterApp() ? 'twitter app' : null;
};

Omniture.prototype.go = function () {
    this.populatePageProperties();
    this.logView();
    //mediator.emit('analytics:ready');
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

            this.pageviewSent = true;
            //mediator.emit('module:analytics:omniture:pageview:sent');
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

