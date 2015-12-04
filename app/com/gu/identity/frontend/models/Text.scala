package com.gu.identity.frontend.models

import play.api.i18n.Messages
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

case class SignInPageText (
                            title: String = Messages("signin.title"),
                            pageTitle: String = Messages("signin.pagetitle"),
                            prelude: String = Messages("signin.prelude"),
                            preludeMoreInfo: String = Messages("signin.prelude.moreinfo"),
                            preludeFaq: String = Messages("signin.prelude.faq"),
                            email: String = Messages("signin.email"),
                            divideText: String = Messages("signin.dividetext"),
                            password: String = Messages("signin.password"),
                            forgottenPassword: String = Messages("signin.forgottenpassword"),
                            rememberMe: String = Messages("signin.rememberme"),
                            signIn: String = Messages("signin.signin"),
                            noAccount: String = Messages("signin.noaccount"),
                            signUp: String = Messages("signin.signup"),
                            conditions: String = Messages("signin.conditions"),
                            termsOfService: String = Messages("signin.termsofservice"),
                            privacyPolicy: String = Messages("signin.privacypolicy")
                            )

case class LayoutText (
                        layoutPageTitle: String = Messages("layout.pagetitle")
                        )

case class SocialSignInText (
                              facebook: String = Messages("socialsignin.description", "Facebook"),
                              google: String = Messages("socialsignin.description", "Google")
                              )

case class HeaderText (
                      back: String = Messages("header.backtext"),
                      logo: String = Messages("header.logo")
                        )

case class FooterText (
                      membership: String = Messages("footer.membership"),
                      jobs: String = Messages("footer.jobs"),
                      dating: String = Messages("footer.dating"),
                      masterclasses: String = Messages("footer.masterclasses"),
                      labs: String = Messages("footer.labs"),
                      subscribe: String = Messages("footer.subscribe"),
                      topics: String = Messages("footer.topics"),
                      contributors: String = Messages("footer.contributors"),
                      about: String = Messages("footer.about"),
                      contact: String = Messages("footer.contact"),
                      techFeedback: String = Messages("footer.techfeedback"),
                      complaints: String = Messages("footer.complaints"),
                      terms: String = Messages("footer.terms"),
                      privacy: String = Messages("footer.privacy"),
                      cookie: String = Messages("footer.cookie"),
                      secureDrop: String = Messages("footer.securedrop"),
                      copyright: String = Messages("footer.copyright")
                        )
