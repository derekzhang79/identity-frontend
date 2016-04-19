package com.gu.identity.frontend.views.models

import com.gu.identity.frontend.models.{GroupCode, GuardianJobs, GuardianTeachersNetwork}
import com.gu.identity.frontend.models.text.{JobsTermsText, TeachersTermsText, TermsText}
import play.api.i18n.Messages

sealed trait TermsViewModel{
  def conditionsText: String
  def termsOfServiceText: String
  def privacyPolicyText: String
  def termsOfServiceUrl: String
  def privacyPolicyUrl: String
}

case class BasicTermsViewModel private(
    conditionsText: String,
    termsOfServiceText: String,
    termsOfServiceUrl: String,
    privacyPolicyText: String,
    privacyPolicyUrl: String)
  extends ViewModel with TermsViewModel {
}

case class TeachersTermsViewModel private(
    conditionsText: String,
    termsOfServiceText: String,
    termsOfServiceUrl: String,
    privacyPolicyText: String,
    privacyPolicyUrl: String,
    extendedConditionsText: String,
    extendedTermsOfServiceUrl: String,
    extendedPrivacyPolicyUrl: String)
  extends ViewModel with TermsViewModel {
  val extendedTerms: Boolean = true
}

case class JobsTermsViewModel(
    conditionsText: String,
    termsOfServiceText: String,
    termsOfServiceUrl: String,
    privacyPolicyText: String,
    privacyPolicyUrl: String,
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
      termsOfServiceUrl = text.termsOfServiceUrl,
      privacyPolicyText = text.privacyPolicyText,
      privacyPolicyUrl = text.privacyPolicyUrl
    )
  }
}

object TeachersTermsViewModel {
  def apply()(implicit messages: Messages): TeachersTermsViewModel = {
    val text = TeachersTermsText()
    TeachersTermsViewModel(
      conditionsText = text.basicTermsText.conditionsText,
      termsOfServiceText = text.basicTermsText.termsOfServiceText,
      termsOfServiceUrl = text.basicTermsText.termsOfServiceUrl,
      privacyPolicyText = text.basicTermsText.privacyPolicyText,
      privacyPolicyUrl = text.basicTermsText.privacyPolicyUrl,
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
      termsOfServiceUrl = text.basicTermsText.termsOfServiceUrl,
      privacyPolicyText = text.basicTermsText.privacyPolicyText,
      privacyPolicyUrl = text.basicTermsText.privacyPolicyUrl,
      extendedConditionsText = text.conditionsText,
      extendedTermsOfServiceUrl = "https://jobs.theguardian.com/terms-and-conditions/",
      extendedPrivacyPolicyUrl = "https://jobs.theguardian.com/privacy-policy/"
    )
  }
}

object Terms {
  def getTermsModel(group: Option[GroupCode])(implicit messages: Messages): TermsViewModel = {
    group match {
      case Some(GuardianTeachersNetwork) => {TeachersTermsViewModel()}
      case Some(GuardianJobs) => JobsTermsViewModel()
      case _ => BasicTermsViewModel()
    }
  }
}
