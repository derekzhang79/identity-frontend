package test.util

import com.github.nscala_time.time.Imports._
import com.gu.identity.testing.usernames.TestUsernames

class TestUser {
  private val testUsers = TestUsernames(
    com.gu.identity.testing.usernames.Encoder.withSecret(Config.testUsersSecret),
    recency = 2.days.standardDuration
  )

  val username = testUsers.generate()
}
