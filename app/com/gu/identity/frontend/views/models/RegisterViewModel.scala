package com.gu.identity.frontend.views.models

import buildinfo.BuildInfo
import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.controllers.routes
import com.gu.identity.frontend.csrf.CSRFToken
import com.gu.identity.frontend.models._
import com.gu.identity.frontend.models.text.RegisterText
import com.gu.identity.frontend.mvt._
import com.gu.identity.frontend.request.RegisterActionRequestBody.FormMapping
import play.api.i18n.Messages


case class RegisterViewModel(
                              layout: LayoutViewModel,

                              oauth: OAuthRegistrationViewModel,

                              registerPageText: RegisterText,
                              terms: TermsViewModel,

                              hasErrors: Boolean,
                              errors: RegisterErrorViewModel,
                              showStandfirst: Boolean,
                              askForPhoneNumber: Boolean,
                              hideDisplayName: Boolean,

                              csrfToken: Option[CSRFToken],
                              returnUrl: String,
                              skipConfirmation: Boolean,
                              clientId: Option[ClientID],
                              group: Option[GroupCode],
                              email: Option[String],

                              shouldCollectConsents: Boolean,

                              actions: RegisterActions,
                              links: RegisterLinks,

                              resources: Seq[PageResource with Product],
                              indirectResources: Seq[PageResource with Product],
                              countryCodes: Option[CountryCodes],
                              gitCommitId: String,
                              emailValidationRegex: String
  )
  extends ViewModel with ViewModelResources


object RegisterViewModel {

  def apply(
      configuration: Configuration,
      activeTests: ActiveMultiVariantTests,
      errors: Seq[String],
      csrfToken: Option[CSRFToken],
      returnUrl: ReturnUrl,
      skipConfirmation: Option[Boolean],
      clientId: Option[ClientID],
      group: Option[GroupCode],
      email: Option[String],
      shouldCollectConsents: Boolean)
      (implicit messages: Messages): RegisterViewModel = {

    val layout = LayoutViewModel(configuration, activeTests, clientId, Some(returnUrl))

    val codes = countryCodes(clientId)

    RegisterViewModel(
      layout = layout,

      oauth = OAuthRegistrationViewModel(configuration, returnUrl, skipConfirmation, clientId, group, activeTests),

      registerPageText = RegisterText.loadText(clientId),
      terms = Terms.getTermsModel(group),

      hasErrors = errors.nonEmpty,
      errors = RegisterErrorViewModel(errors),

      showStandfirst = showStandfirst(clientId),
      askForPhoneNumber = askForPhoneNumber(clientId),
      hideDisplayName = true,

      csrfToken = csrfToken,
      returnUrl = returnUrl.url,
      skipConfirmation = skipConfirmation.getOrElse(false),
      clientId = clientId,
      group = group,
      email = email,

      shouldCollectConsents = shouldCollectConsents,

      actions = RegisterActions(),
      links = RegisterLinks(returnUrl, skipConfirmation, clientId),

      resources = layout.resources,
      indirectResources = layout.indirectResources,

      countryCodes = codes,
      gitCommitId = BuildInfo.gitCommitId,
      emailValidationRegex = FormMapping.dotlessDomainEmailRegex.pattern.toString
    )
  }

  private def showStandfirst(clientId: Option[ClientID]) =
    clientId.contains(GuardianJobsClientID)

  private def askForPhoneNumber(clientId: Option[ClientID]) =
    clientId.contains(GuardianCommentersClientID)

  private def countryCodes(clientId: Option[ClientID]) : Option[CountryCodes] = {
    clientId match {
      case Some(c) => if (c.id == "comments") Option(CountryCodes.apply) else None
      case _ => None
    }
  }
}


case class RegisterActions private(
    register: String)

object RegisterActions {
  def apply(): RegisterActions =
    RegisterActions(
      register = routes.RegisterAction.register().url
    )
}


case class RegisterLinks private(
    signIn: String)

object RegisterLinks {
  def apply(returnUrl: ReturnUrl, skipConfirmation: Option[Boolean], clientId: Option[ClientID]): RegisterLinks =
    RegisterLinks(
      signIn = UrlBuilder(routes.Application.signIn().url, returnUrl, skipConfirmation, clientId, group = None)
    )
}
