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
