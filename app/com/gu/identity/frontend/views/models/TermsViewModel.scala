package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.models.text.TermsText
import play.api.i18n.Messages


case class TermsViewModel private(
    conditionsText: String,
    termsOfServiceText: String,
    privacyPolicyText: String,
    termsOfServiceUrl: String = TermsViewModel.termsOfServiceUrl,
    privacyPolicyUrl: String = TermsViewModel.privacyPolicyUrl)
  extends ViewModel

object TermsViewModel {

  val termsOfServiceUrl = "http://www.theguardian.com/help/terms-of-service"
  val privacyPolicyUrl = "http://www.theguardian.com/help/privacy-policy"

  def apply()(implicit messages: Messages): TermsViewModel = {
    val text = TermsText()
    TermsViewModel(
      conditionsText = text.conditionsText,
      termsOfServiceText = text.termsOfServiceText,
      privacyPolicyText = text.privacyPolicyText
    )
  }
}
