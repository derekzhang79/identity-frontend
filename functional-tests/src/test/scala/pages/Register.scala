package test.pages

import test.util.user.EmailTestUser
import test.util.{LoadablePage, Browser, Config}

class Register(val testUser: EmailTestUser) extends LoadablePage with Browser {
  val url = s"${Config.baseUrl}/register"

  def fillInPersonalDetails(): Unit = RegisterFields.fillIn()

  def submit(): Unit = {
    val selector = className("submit-input")
    assert(pageHasElement(selector))
    click on selector
  }

  def hasLoaded(): Boolean = pageHasElement(className("submit-input"))

  def registerWithFacebook(): Unit = {
    assert(pageHasElement(registerWithFacebookButton))
    click.on(registerWithFacebookButton)
  }

  private object RegisterFields {
    val firstName = textField(id("user_firstName"))
    val lastName = textField(id("user_secondName"))
    val email = emailField(id("user_primaryEmailAddress"))
    val username = textField(id("user_publicFields_username"))
    val password = pwdField(id("user_password"))

    def fillIn() = {
      assert(pageHasElement(id("user_password")))

      firstName.value = testUser.name
      lastName.value = testUser.name
      email.value = s"${testUser.name}@gu.com"
      username.value = testUser.name
      password.value = testUser.name
    }
  }

  private lazy val registerWithFacebookButton =
    cssSelector("a[data-test-id='facebook-sign-in']")
}
