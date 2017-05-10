package test.util.user

import com.github.nscala_time.time.Imports._
import com.gu.identity.testing.usernames.TestUsernames
import test.util.Config

class EmailTestUser() {
  private val testUsers = TestUsernames(
    com.gu.identity.testing.usernames.Encoder.withSecret(Config.testUsersSecret),
    recency = 2.days.standardDuration
  )

  val name = testUsers.generate()
  val email = s"${name}@gu.com"
  val password = name
}
