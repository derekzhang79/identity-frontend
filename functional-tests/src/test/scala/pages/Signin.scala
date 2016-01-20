package test.pages

import test.util.user.EmailTestUser
import test.util.{Browser, Config}
import org.scalatest.selenium.Page

class Signin(val testUser: EmailTestUser) extends Page with Browser {
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

    if (Config.stage == "CODE") {
      val selector = cssSelector("a[data-test-id='facebook-sign-in']")
      pageHasElement(selector)
      click.on(selector)
    } else { // PROD
      assert(pageHasElement(signInWithFacebookButton))
      click.on(signInWithFacebookButton)
    }
  }

  def pageHasLoaded(): Boolean = {

    if (Config.stage == "CODE")
      pageHasElement(cssSelector("a[data-test-id='register-link']"))
    else // PROD
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

      emailAddress.value = s"${testUser.name}@gu.com"
      password.value = testUser.name
    }
  }

  private lazy val signUpLink = id("register_cta")

  private lazy val signInButton = id("signin_submit")

  private lazy val signInWithFacebookButton = id("social_signin_cta_facebook")
}
