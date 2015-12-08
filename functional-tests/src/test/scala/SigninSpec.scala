package test

import test.pages.RegisterConfirm
import test.util.{TestUser, WebBrowserUtil, Config}
import org.scalatest.selenium.WebBrowser
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FeatureSpec, GivenWhenThen}
import org.slf4j.LoggerFactory

class SigninSpec extends FeatureSpec with WebBrowser with WebBrowserUtil
  with GivenWhenThen with BeforeAndAfter with BeforeAndAfterAll  {

  def logger = LoggerFactory.getLogger(this.getClass)

  before { // Before each test
    resetDriver()
  }

  override def beforeAll() = {
    Config.printSummary()
  }

  // After all tests execute, close all windows, and exit the driver
  override def afterAll() = {
    Config.driver.quit()
  }

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
        assert(cookiesSet.map(_.getName).contains(idCookie))
      }

      When("I click 'Confirm Registration' button")
      registerConfirm.confirmRegistration()

      Then("I should land back on 'Sign in' page.")
      assert(signin.pageHasLoaded())

      When("I fill in credentials")
      signin.fillInCredentials()

      And("I click on 'Sign in' button")
      signin.signIn()

      Then("I should land on 'Guardian Homepage'")
      val homepage = new pages.Homepage
      assert(homepage.pageHasLoaded())

      And("I should be signed in.")
      assert(homepage.userDisplayName == testUser.username)
    }
  }
}