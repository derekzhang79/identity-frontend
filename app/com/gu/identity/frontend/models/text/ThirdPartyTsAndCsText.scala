package com.gu.identity.frontend.models.text

import com.gu.identity.frontend.models.{GuardianJobs, GuardianTeachersNetwork, GroupCode, ReturnUrl}
import play.api.i18n.Messages

case class BaseTsAndCsText private(
    title: String,
    explanationText: String,
    continueButtonText: String,
    termsText: String,
    termsOfServiceLinkText: String,
    privacyPolicyLinkText: String)

object BaseTsAndCsText {
  def apply(title: String, serviceName: String)(implicit messages: Messages): BaseTsAndCsText = {
    BaseTsAndCsText(
      title = "Welcome to Guardian",
      explanationText = "By connecting your account you get access to a range of features:",
      continueButtonText = "Continue",
      termsText = "By proceeding, you agree to Guardian",
      termsOfServiceLinkText = "Terms of Service",
      privacyPolicyLinkText = "Privacy Policy"
    )
  }
}

case class ThirdPartyTsAndCsText (
    title: String,
    features: Seq[String],
    continueLink: String,
    serviceName: String,
    termsOfServiceLink: String,
    privacyPolicyLink: String,
    baseText: BaseTsAndCsText)

object TeachersTsAndCsText {
  def apply(returnUrl: ReturnUrl)(implicit messages: Messages): ThirdPartyTsAndCsText = {
    val name = "Teacher's Network"
    ThirdPartyTsAndCsText(
      title = name,
      features = Seq("Download teaching resources"),
      continueLink = returnUrl.url,
      serviceName = name,
      termsOfServiceLink = "http://teachers.theguardian.com/Terms.htm",
      privacyPolicyLink = "http://teachers.theguardian.com/privacypolicy.htm",
      baseText = BaseTsAndCsText(name, name)
    )
  }
}

object JobsTsAndCsText {
  def apply(returnUrl: ReturnUrl)(implicit messages: Messages): ThirdPartyTsAndCsText = {
    val title = "Jobs"
    val serviceName = "Jobs'"
    ThirdPartyTsAndCsText(
      title = title,
      features = Seq(
        "Receive relevant jobs into your inbox",
        "Upload your CV and let potential employers find you",
        "Shortlist jobs that interest you, so that you can come back to them later"),
      continueLink = returnUrl.url,
      serviceName = serviceName,
      termsOfServiceLink = "https://jobs.theguardian.com/terms-and-conditions/",
      privacyPolicyLink = "https://jobs.theguardian.com/privacy-policy/",
      baseText = BaseTsAndCsText(title, serviceName)
    )
  }
}

object TsAndCsPageText {
  def getPageText(group: GroupCode, returnUrl: ReturnUrl)(implicit messages: Messages): ThirdPartyTsAndCsText = {
    group match {
      case GuardianTeachersNetwork => TeachersTsAndCsText(returnUrl)
      case GuardianJobs => JobsTsAndCsText(returnUrl)
    }
  }
}
