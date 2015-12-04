package test.pages

import test.util.{Util, TestUser, Config}
import org.scalatest.selenium.{Page, WebBrowser}

class Register(val testUser: TestUser) extends Page with WebBrowser with Util {
  val url = s"${Config.baseUrl}/register"

  def fillInPersonalDetails(): Unit = {
    RegisterFields.fillIn()
  }

  def submit(): Unit = {
    val selector = className("submit-input")
    assert(pageHasElement(selector))
    click.on(selector)
  }

  def pageHasLoaded(): Boolean = {
    pageHasElement(className("submit-input"))
  }

  private object RegisterFields {
    val firstName = textField(id("user_firstName"))
    val lastName = textField(id("user_secondName"))
    val email = emailField(id("user_primaryEmailAddress"))
    val username = textField(id("user_publicFields_username"))
    val password = pwdField(id("user_password"))

    def fillIn() = {
      assert(pageHasElement(id("user_password")))

      firstName.value = testUser.username
      lastName.value = testUser.username
      email.value = s"${testUser.username}@gu.com"
      username.value = testUser.username
      password.value = testUser.username
    }
  }
}
