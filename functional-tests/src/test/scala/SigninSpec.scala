package test

import test.util.user.{FacebookTestUserService, EmailTestUser}
import test.pages.{FacebookAuthDialog, FacebookLogin, RegisterConfirm}
import test.util._
import org.scalatest.selenium.WebBrowser
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FeatureSpec, GivenWhenThen}
import org.slf4j.LoggerFactory

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

  feature("Sign in") {
    scenario("Users register with Email.") {
      val testUser = new EmailTestUser

      Given("users have not registered with email before,")

      When("they visit 'Sign in' page,")
      val signin = new pages.Signin(testUser)
      go.to(signin)
      assert(signin.pageHasLoaded())

      And("click on 'Sign up' link,")
      signin.signUp()

      Then("they should land on 'Register' page.")
      val register = new pages.Register(testUser)
      assert(register.hasLoaded())

      When("Users fill in personal details,")
      register.fillInPersonalDetails()

      And("click on 'Create account' button,")
      register.submit()

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
      assert(elementHasText(className("js-profile-info"), testUser.name))
    }

    scenario("Users register with Facebook.") {
      val fbTestUser = FacebookTestUserService.createUser()
      assert(fbTestUser.created)

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

      val fbTestUserIsDeleted = FacebookTestUserService.deleteUser(fbTestUser)
      assert(fbTestUserIsDeleted)
    }

    scenario("Users attempt to register with Facebook without granting email permissions.") {
      val fbTestUser = FacebookTestUserService.createUser()
      assert(fbTestUser.created)

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

      val fbTestUserIsDeleted = FacebookTestUserService.deleteUser(fbTestUser)
      assert(fbTestUserIsDeleted)
    }
  }
}
