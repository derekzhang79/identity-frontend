package test.pages

import test.util.user.EmailTestUser
import test.util.{LoadablePage, Browser, Config}

class Register(val testUser: EmailTestUser) extends LoadablePage with Browser {
  val url = s"${Config.baseUrl}/register"

  def hasLoaded(): Boolean = pageHasElement(createAccountButton)

  def fillInPersonalDetails(): Unit = RegisterFields.fillIn()

  def createAccount(): Unit = clickOn(createAccountButton)

  private object RegisterFields {
    val firstName = id("register_field_firstname")
    val lastName = id("register_field_lastname")
    val email = id("register_field_email")
    val username = id("register_field_username")
    val password = id("register_field_password")

    def fillIn() = {
      setValue(firstName, testUser.name)
      setValue(lastName, testUser.name)
      setValue(email, s"${testUser.name}@gu.com")
      setValue(username, testUser.name)
      setValue(password, testUser.name)
    }
  }

  private lazy val createAccountButton = id("register_submit")
}
