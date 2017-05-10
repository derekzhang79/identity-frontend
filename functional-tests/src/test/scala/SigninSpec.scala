package test

import test.util._
import test.util.user.EmailTestUser
import test.pages.RegisterConfirm
import org.scalatest.selenium.WebBrowser
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FeatureSpec, GivenWhenThen}
import org.slf4j.LoggerFactory

class SigninSpec extends FeatureSpec with WebBrowser with Browser
  with GivenWhenThen with BeforeAndAfter with BeforeAndAfterAll {

  def logger = LoggerFactory.getLogger(this.getClass)

  before { /* each test */ Driver.reset() }

  override def beforeAll() = Config.printSummary()

  // After all tests execute, close all windows, and exit the driver
  override def afterAll() = Driver.quit()

  scenario("Users register with Email.") {
    val emailTestUser = new EmailTestUser()

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
  }
}
