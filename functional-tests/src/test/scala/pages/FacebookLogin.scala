package test.pages

import test.util.{Browser, Config}
import org.scalatest.selenium.Page

class FacebookLogin extends Page with Browser {
  val url = "https://www.facebook.com/login.php"

  def pageHasLoaded(): Boolean = pageHasElement(logInButton)

  def fillInCredentials(): Unit = CredentialsFields.fillIn()

  def logIn(): Unit = click.on(logInButton)

  private object CredentialsFields {
    val email = textField(id("email"))
    val password = pwdField(id("pass"))

    def fillIn() = {
      assert(pageHasElement(logInButton))

      email.value = Config.FacebookCredentials.email
      password.value = Config.FacebookCredentials.password
    }
  }

  private lazy val logInButton = id("u_0_2")
}

