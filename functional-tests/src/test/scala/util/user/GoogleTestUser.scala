package test.util.user

import test.util.Config

object GoogleTestUser extends TestUser {
  val name = Config.GoogleTestUserCredentials.name
  val email = Config.GoogleTestUserCredentials.email
  val password = Config.GoogleTestUserCredentials.password
}
