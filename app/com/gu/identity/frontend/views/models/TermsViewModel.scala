package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.models.text.{JobsTermsText, TeachersTermsText, TermsText}
import play.api.i18n.Messages

sealed trait TermsViewModel{
  def conditionsText: String
  def termsOfServiceText: String
  def privacyPolicyText: String
  val termsOfServiceUrl: String = "http://www.theguardian.com/help/terms-of-service"
  val privacyPolicyUrl: String = "http://www.theguardian.com/help/privacy-policy"
}

case class BasicTermsViewModel private(
    conditionsText: String,
    termsOfServiceText: String,
    privacyPolicyText: String)
  extends ViewModel with TermsViewModel {
}

case class TeachersTermsViewModel private(
    conditionsText: String,
    termsOfServiceText: String,
    privacyPolicyText: String,
    extendedConditionsText: String,
    extendedTermsOfServiceUrl: String,
    extendedPrivacyPolicyUrl: String)
  extends ViewModel with TermsViewModel {
  val extendedTerms: Boolean = true
}

case class JobsTermsViewModel(
    conditionsText: String,
    termsOfServiceText: String,
    privacyPolicyText: String,
    extendedConditionsText: String,
    extendedTermsOfServiceUrl: String,
    extendedPrivacyPolicyUrl: String)
  extends ViewModel with TermsViewModel {
  val extendedTerms: Boolean = true
}

object BasicTermsViewModel {
  def apply()(implicit messages: Messages): BasicTermsViewModel = {
    val text = TermsText()
    BasicTermsViewModel(
      conditionsText = text.conditionsText,
      termsOfServiceText = text.termsOfServiceText,
      privacyPolicyText = text.privacyPolicyText
    )
  }
}

object TeachersTermsViewModel {
  def apply()(implicit messages: Messages): TeachersTermsViewModel = {
    val text = TeachersTermsText()
    TeachersTermsViewModel(
      conditionsText = text.basicTermsText.conditionsText,
      termsOfServiceText = text.basicTermsText.termsOfServiceText,
      privacyPolicyText = text.basicTermsText.privacyPolicyText,
      extendedConditionsText = text.conditionsText,
      extendedTermsOfServiceUrl = "http://teachers.theguardian.com/Terms.htm",
      extendedPrivacyPolicyUrl = "http://teachers.theguardian.com/privacypolicy.htm"
    )
  }
}

object JobsTermsViewModel {
  def apply()(implicit messages: Messages): JobsTermsViewModel = {
    val text = JobsTermsText()
    JobsTermsViewModel(
      conditionsText = text.basicTermsText.conditionsText,
      termsOfServiceText = text.basicTermsText.termsOfServiceText,
      privacyPolicyText = text.basicTermsText.privacyPolicyText,
      extendedConditionsText = text.conditionsText,
      extendedTermsOfServiceUrl = "https://jobs.theguardian.com/terms-and-conditions/",
      extendedPrivacyPolicyUrl = "https://jobs.theguardian.com/privacy-policy/"
    )
  }
}

object Terms {
  def getTermsModel(groupCode: Option[String])(implicit messages: Messages): TermsViewModel = {
    groupCode match {
      case Some("GTNF") => {TeachersTermsViewModel()}
      case Some("GRS") => JobsTermsViewModel()
      case _ => BasicTermsViewModel()
    }
  }
}
