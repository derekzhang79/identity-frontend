package com.gu.identity.frontend.views.models


case class SignInViewModel(title: String = "Sign in to the Guardian", pageTitle: String = "Sign in") extends ViewModel {
  def toMap =
    Map("title" -> title, "pageTitle" -> pageTitle)
}
