package test.pages

import test.util.{Browser, TestUser, Config}
import org.scalatest.selenium.Page

class Signin(val testUser: TestUser) extends Page with Browser {
  val url = s"${Config.baseUrl}/signin"

  def signUp() = {
    assert(pageHasElement(signUpLink))
    click.on(signUpLink)
  }

  def signIn() = {
    assert(pageHasElement(signInButton))
    click.on(signInButton)
  }

  def signInWithFacebook() = {
    assert(pageHasElement(signInWithFacebookButton))
    click.on(signInWithFacebookButton)
  }

  def pageHasLoaded(): Boolean = {
    pageHasElement(signUpLink)
  }

  def fillInCredentials() = {
    SigninFields.fillIn()
  }

  private object SigninFields {
    val emailAddress = emailField(id("signin_field_email"))
    val password = pwdField(id("signin_field_password"))

    def fillIn() = {
      assert(pageHasElement(id("signin_field_password")))

      emailAddress.value = s"${testUser.username}@gu.com"
      password.value = testUser.username
    }
  }

  private lazy val signUpLink = id("register_cta")

  private lazy val signInButton = id("signin_submit")

  private lazy val signInWithFacebookButton = id("social_signin_cta_facebook")
}
