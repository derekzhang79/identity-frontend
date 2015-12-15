package com.gu.identity.frontend.controllers

import com.gu.identity.frontend.logging.Logging
import com.gu.identity.frontend.services.IdentityService
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}


class RegisterAction(identityService: IdentityService, val messagesApi: MessagesApi) extends Controller with Logging with I18nSupport {

  def register = Action.async { implicit request => ???
  }
}
