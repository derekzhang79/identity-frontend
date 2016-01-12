package test

import test.pages.{FacebookLogin, RegisterConfirm}
import test.util.{TestUser, Browser, Config, Driver}
import org.scalatest.selenium.WebBrowser
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FeatureSpec, GivenWhenThen}
import org.slf4j.LoggerFactory

class SigninSpec extends FeatureSpec with WebBrowser with Browser
  with GivenWhenThen with BeforeAndAfter with BeforeAndAfterAll  {

  def logger = LoggerFactory.getLogger(this.getClass)

  before { /* each test */
    Driver.reset()
    Driver.addCookie("GU_PROFILE_BETA", "1") // required due to A/B testing
  }

  override def beforeAll() = Config.printSummary()

  // After all tests execute, close all windows, and exit the driver
  override def afterAll() = Driver.quit()

  feature("Sign in") {
    scenario("User signs in with newly created Identity account") {
      val testUser = new TestUser

      When("I visit 'Sign in' page ")
      val signin = new pages.Signin(testUser)
      go.to(signin)
      assert(signin.pageHasLoaded())

      And("I click on 'Sign up' link")
      signin.signUp()

      Then("I should land on 'Register' page.")
      val register = new pages.Register(testUser)
      assert(register.pageHasLoaded())

      When("I fill in personal details")
      register.fillInPersonalDetails()

      And("I submit the form to create my new Identity account")
      register.submit()

      Then("I should land on 'Registration Confirmation' page")
      val registerConfirm = new RegisterConfirm
      assert(registerConfirm.pageHasLoaded())

      And("I should have Identity cookies.")
      Seq("GU_U", "SC_GU_U", "SC_GU_LA").foreach { idCookie =>
        assert(Driver.cookiesSet.map(_.getName).contains(idCookie))
      }

      When("I click 'Confirm Registration' button")
      registerConfirm.confirmRegistration()

      Then("I should land on 'Guardian Homepage'")
      val homepage = new pages.Homepage
      assert(homepage.pageHasLoaded())

      And("I should be signed in.")
      assert(elementHasText(className("js-profile-info"), testUser.username))
    }

    scenario("User signs in with Facebook") {
      val testUser = new TestUser

      When("I visit 'Sign in' page ")
      val signin = new pages.Signin(testUser)
      go.to(signin)
      assert(signin.pageHasLoaded())

      And("I click on 'Sign in with Facebook' button")
      signin.signInWithFacebook()

      Then("I should land on 'Facebook Login' page.")
      val facebookLogin = new FacebookLogin()
      assert(facebookLogin.pageHasLoaded())

      When("I fill in Facebook credentials")
      facebookLogin.fillInCredentials()

      And("I click on 'Log In' button")
      facebookLogin.logIn()

      Then("I should land on 'Guardian Homepage'")
      val homepage = new pages.Homepage
      assert(homepage.pageHasLoaded())

      And("I should be signed in.")
      assert(elementHasText(className("js-profile-info"), Config.FacebookCredentials.name))
    }
  }
}
