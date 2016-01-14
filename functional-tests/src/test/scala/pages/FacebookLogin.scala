package test.pages

import test.util.{LoadablePage, Browser}
import util.user.FacebookTestUser

class FacebookLogin extends LoadablePage with Browser {
  val url = "https://www.facebook.com/login.php"

  def hasLoaded(): Boolean = pageHasElement(logInButton)

  def fillInCredentials(fbTestUser: FacebookTestUser): Unit = {

  (fbTestUser.email, fbTestUser.password) match {
      case (Some(email), Some(password)) => CredentialsFields.fillIn(email, password)
      case _ => new IllegalStateException("FacebookTestUser missing password.")
    }
  }

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

