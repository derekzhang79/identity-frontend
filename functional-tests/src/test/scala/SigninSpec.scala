package test

import test.util._
import test.util.user.{GoogleTestUser, FacebookTestUser, FacebookTestUserService, EmailTestUser}
import test.util.Config.ResetPasswordEmail
import test.pages.{FacebookAuthDialog, FacebookLogin, RegisterConfirm}
import org.scalatest.selenium.WebBrowser
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FeatureSpec, GivenWhenThen}
import org.slf4j.LoggerFactory
import java.util.Calendar
import test.util.Mail

class SigninSpec extends FeatureSpec with WebBrowser with Browser
  with GivenWhenThen with BeforeAndAfter with BeforeAndAfterAll {

  def logger = LoggerFactory.getLogger(this.getClass)

  before { /* each test */
    Driver.reset()
    Driver.addCookie("GU_PROFILE_BETA", "1") // required due to A/B testing
  }

  override def beforeAll() = Config.printSummary()

  // After all tests execute, close all windows, and exit the driver
  override def afterAll() = Driver.quit()

  /* Sign in into existing Google Account with */
  def googleFixture(): Unit = {

    // When they visit 'Google Accounts' page
    val googleAccounts = new pages.GoogleAccounts
    go.to(googleAccounts)
    assert(googleAccounts.hasLoaded())

    // and sign in with their Google credentials
    googleAccounts.signIn()

    // then they should land on 'Google My Account' page
    assert(pageHasUrl("https://myaccount.google.com"))
  }

  def withFacebookTestUserFixture(testFun: FacebookTestUser => Any) {

    val fbTestUser = FacebookTestUserService.createUser() // create fixture
    assert(fbTestUser.created)

    try {
      testFun(fbTestUser) // "loan" fixture to the test
    }
    finally {
      val fbTestUserIsDeleted = FacebookTestUserService.deleteUser(fbTestUser) // clean up fixture
      assert(fbTestUserIsDeleted)
    }
  }

  def withEmailTestUserFixture(testFun: EmailTestUser => Any) {
    val emailTestUser = new EmailTestUser
    testFun(emailTestUser)
  }

  feature("Sign in") {
    scenario("Users register with Email.") {
      withEmailTestUserFixture { emailTestUser =>

        Given("users have not registered with email before,")

        When("they visit 'Sign in' page,")
        val signin = new pages.Signin(emailTestUser)
        go.to(signin)
        assert(signin.pageHasLoaded())

        And("click on 'Sign up' link,")
        signin.signUp()

        Then("they should land on 'Register' page.")
        val register = new pages.Register(emailTestUser)
        assert(register.hasLoaded())

        When("Users fill in personal details,")
        register.fillInPersonalDetails()

        And("click on 'Create account' button,")
        register.createAccount()

        Then("they should land on 'Registration Confirmation' page,")
        val registerConfirm = new RegisterConfirm
        assert(registerConfirm.hasLoaded())

        And("they should have Identity cookies.")
        Seq("GU_U", "SC_GU_U", "SC_GU_LA").foreach { idCookie =>
          assert(Driver.cookiesSet.map(_.getName).contains(idCookie))
        }

        When("Users click on 'Confirm Registration' button,")
        registerConfirm.confirmRegistration()

        Then("they should land on 'Guardian Homepage',")
        val homepage = new pages.Homepage
        assert(homepage.hasLoaded())

        And("should be signed in.")
        assert(elementHasText(className("js-profile-info"), emailTestUser.name))
      }
    }

    scenario("Users register with Facebook.") {
      withFacebookTestUserFixture { fbTestUser =>

        Given("users have not registered with Facebook before,")

        When("they visit 'Sign in' page")
        val signin = new pages.Signin(new EmailTestUser)
        go.to(signin)
        assert(signin.pageHasLoaded())

        And("click on 'Sign in with Facebook' button")
        signin.signInWithFacebook()

        Then("they should land on 'Facebook Login' page.")
        val facebookLogin = new FacebookLogin()
        assert(facebookLogin.hasLoaded())

        When("users fill in Facebook credentials")
        facebookLogin.fillInCredentials(fbTestUser)

        And("click on 'Log In' button")
        facebookLogin.logIn()

        Then("Facebook auth dialog should open.")
        val facebookAuthDialog = new FacebookAuthDialog
        assert(facebookAuthDialog.hasLoaded())

        When("users clicks on 'Okay' button")
        facebookAuthDialog.confirm()

        Then("they should land on 'Guardian Homepage'")
        val homepage = new pages.Homepage
        assert(homepage.hasLoaded())

        And("should be signed in.")
        assert(elementHasText(className("js-profile-info"), fbTestUser.name))
      }
    }

    scenario("Users attempt to register with Facebook without granting email permissions.") {
      withFacebookTestUserFixture { fbTestUser =>

        Given("users have not registered with Facebook before,")

        When("they visit 'Sign in' page,")
        val signin = new pages.Signin(new EmailTestUser)
        go.to(signin)
        assert(signin.pageHasLoaded())

        And("click on 'Sign in with Facebook' button")
        signin.signInWithFacebook()

        Then("they should land on 'Facebook Login' page.")
        val facebookLogin = new FacebookLogin()
        assert(facebookLogin.hasLoaded())

        When("users fill in Facebook credentials,")
        facebookLogin.fillInCredentials(fbTestUser)

        And("click on 'Log In' button,")
        facebookLogin.logIn()

        Then("'Facebook Auth Dialog' should open.")
        val facebookAuthDialog = new FacebookAuthDialog
        assert(facebookAuthDialog.hasLoaded())

        When("users click on 'Edit the info you provide' link,")
        facebookAuthDialog.editProvidedInfo()

        Then("'Edit Provided Info' dialog should open.")
        val facebookProvidedInfoDialog = new pages.FacebookProvidedInfoDialog
        assert(facebookProvidedInfoDialog.hasLoaded())

        When("users un-check granting email permissions to Guardian,")
        facebookProvidedInfoDialog.uncheckEmailPermission()

        And("click on 'Okay' button,")
        facebookAuthDialog.confirm()

        Then("they should be rejected back to 'Guardian Register' page,")
        val register = new pages.Register(new EmailTestUser)
        assert(register.hasLoaded())

        And("should see Facebook error message.")
        pageHasUrl("error=fbEmail")
      }
    }

    scenario("Existing users sign in with Google.") {
      googleFixture()

      Given("users have already registered with Guardian via Google " +
        "and are signed in their Google account,")

      When("they visit 'Sign in' page,")
      val signin = new pages.Signin
      go.to(signin)
      assert(signin.pageHasLoaded())

      And("click on 'Sign in with Google' button,")
      signin.signInWithGoogle()

      Then("they should land on 'Guardian Homepage'")
      val homepage = new pages.Homepage
      assert(homepage.hasLoaded())

      And("should be signed in.")
      assert(elementHasText(className("js-profile-info"), GoogleTestUser.name))
    }

    scenario("Existing users reset their password.") {
      val resetRequestTime = Calendar.getInstance().getTime
      Mail.deleteAllMail


      Given("users have already registered with Guardian via Google,")

      When("they visit 'Reset Password' page,")
      val resetPassword = new pages.ResetPassword
      go.to(resetPassword)
      assert(resetPassword.hasLoaded())

      And("enter their email address,")
      resetPassword.setEmailAddress(ResetPasswordEmail.to)

      And("click 'Reset Password' button,")
      resetPassword.submit()

      Then("they should land on 'Email Sent' page,")
      assert(pageHasUrl("reset/email-sent"))

      And("should receive reset request email ")

      def resetEmailReceived: Boolean = {

        def repeater(count: Int): Boolean = {

          if (count == 0)
            false
          else Mail.resetPasswordEmailReceived(resetRequestTime) match {
            case true => true
            case false =>
              Thread.sleep(5000)
              repeater(count - 1)
          }
        }

        repeater(5)
      }

      assert(resetEmailReceived)
      Mail.deleteAllMail
    }
  }
}
