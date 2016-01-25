package test.pages

import test.util.user.EmailTestUser
import test.util.{LoadablePage, Browser, Config}

class Register(val testUser: EmailTestUser) extends LoadablePage with Browser {
  val url = s"${Config.baseUrl}/register"

  def hasLoaded(): Boolean = pageHasElement(createAccountButton)

  def fillInPersonalDetails(): Unit = RegisterFields.fillIn()

  def createAccount(): Unit = clickOn(createAccountButton)

  private object RegisterFields {
    val firstName = id("user_firstName")
    val lastName = id("user_secondName")
    val email = id("user_primaryEmailAddress")
    val username = id("user_publicFields_username")
    val password = id("user_password")

    def fillIn() = {
      setValue(firstName, testUser.name)
      setValue(lastName, testUser.name)
      setValue(email, s"${testUser.name}@gu.com")
      setValue(username, testUser.name)
      setValue(password, testUser.name)
    }
  }

  private lazy val createAccountButton = className("submit-input")
}
