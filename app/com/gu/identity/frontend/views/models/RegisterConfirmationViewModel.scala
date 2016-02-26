package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.models.{ReturnUrl, ClientID, UrlBuilder}
import com.gu.identity.frontend.models.text.RegisterConfirmationText
import play.api.i18n.Messages

case class RegisterConfirmationViewModel private(
    layout: LayoutViewModel,
    returnUrl: String,
    registerConfirmationPageText: RegisterConfirmationText,
    faqUrl: String = "http://www.theguardian.com/help/identity-faq",
    emailUserHelpUrl: String = "mailto:userhelp@theguardian.com?subject=Account help",
    resetPasswordUrl: String,
    signOutUrl: String,
    clientId: Option[ClientID],
    resources: Seq[PageResource with Product],
    indirectResources: Seq[PageResource with Product])
  extends ViewModel
  with ViewModelResources

object RegisterConfirmationViewModel {
  def apply(configuration: Configuration, returnUrl: ReturnUrl, clientId: Option[ClientID])(implicit messages: Messages): RegisterConfirmationViewModel = {
    val layout = LayoutViewModel(configuration, clientId)

    RegisterConfirmationViewModel(
      layout = layout,
      returnUrl = returnUrl.url,
      registerConfirmationPageText = RegisterConfirmationText(),
      resetPasswordUrl = UrlBuilder("https://profile.theguardian.com/reset", returnUrl, clientId),
      signOutUrl = UrlBuilder("https://profile.theguardian.com/signout", returnUrl, clientId),
      clientId = clientId,
      resources = layout.resources,
      indirectResources = layout.indirectResources
    )
  }
}
