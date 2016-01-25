package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.models.UrlBuilder
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
    resources: Seq[PageResource with Product],
    indirectResources: Seq[PageResource with Product])
  extends ViewModel
  with ViewModelResources

object RegisterConfirmationViewModel {
  def apply(configuration: Configuration, returnUrl: Option[String])(implicit messages: Messages): RegisterConfirmationViewModel = {
    val layout = LayoutViewModel(configuration, None)
    val urlParams = Seq(("returnUrl" -> returnUrl.getOrElse("http://www.theguardian.com")))

    RegisterConfirmationViewModel(
      layout = layout,
      returnUrl = returnUrl.getOrElse("http://www.theguardian.com"),
      registerConfirmationPageText = RegisterConfirmationText(),
      resetPasswordUrl = UrlBuilder("https://profile.theguardian.com/reset",urlParams),
      signOutUrl = UrlBuilder("https://profile.theguardian.com/signout",urlParams),
      resources = layout.resources,
      indirectResources = layout.indirectResources
    )
  }
}
