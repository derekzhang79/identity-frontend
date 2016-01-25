package test.pages

import test.util.{LoadablePage, Browser}
import test.util.user.FacebookTestUser

class FacebookLogin extends LoadablePage with Browser {
  val url = "https://www.facebook.com/login.php"

  def hasLoaded(): Boolean = pageHasElement(logInButton)

  def fillInCredentials(fbTestUser: FacebookTestUser): Unit =
    CredentialsFields.fillIn(fbTestUser.email, fbTestUser.password)


  def logIn(): Unit = click.on(logInButton)

  private object CredentialsFields {
    val email = textField(id("email"))
    val password = pwdField(id("pass"))

    def fillIn(email: String, password: String) = {
      this.email.value = email
      this.password.value = password
    }
  }

  private lazy val logInButton = name("login")
}

