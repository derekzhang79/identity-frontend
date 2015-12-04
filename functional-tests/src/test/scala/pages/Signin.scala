package test.pages

import test.util.{TestUser, Util, Config}
import org.scalatest.selenium.{Page, WebBrowser}

class Signin(val testUser: TestUser) extends Page with WebBrowser with Util {
  val url = s"${Config.baseUrl}/signin"

  private lazy val signUpLink = cssSelector(s"a[href='/register']")
  private lazy val signInButton = id("signin_submit")

  def signUp() = {
    assert(pageHasElement(signUpLink))
    click.on(signUpLink)
  }

  def signIn() = {
    assert(pageHasElement(signInButton))
    click.on(signInButton)
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
}
