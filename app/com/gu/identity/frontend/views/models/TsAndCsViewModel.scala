package com.gu.identity.frontend.views.models


import com.gu.identity.frontend.configuration.Configuration
import com.gu.identity.frontend.models.text._
import com.gu.identity.frontend.controllers._
import com.gu.identity.frontend.models._
import play.api.i18n.Messages

case class TsAndCsViewModel private(
    layout: LayoutViewModel,
    resources: Seq[PageResource with Product],
    indirectResources: Seq[PageResource with Product],
    clientId: Option[ClientID],
    tsAndCsPageText: ThirdPartyTsAndCsText,
    returnUrl: String,
    groupCode: String,
    continueFormUrl: String = routes.ThirdPartyTsAndCs.addToGroup.url
) extends ViewModel with ViewModelResources

object TsAndCsViewModel {
  def apply(
    configuration: Configuration,
    clientId: Option[ClientID],
    group: GroupCode,
    returnUrl: ReturnUrl)(implicit messages: Messages): TsAndCsViewModel = {

    val layout = LayoutViewModel(configuration, clientId)
    TsAndCsViewModel(
      layout = layout,
      resources = layout.resources,
      indirectResources = layout.indirectResources,
      clientId = clientId,
      tsAndCsPageText = TsAndCsPageText.getPageText(group),
      groupCode = group.getCodeValue,
      returnUrl = returnUrl.url
    )
  }
}

