package test.util.user

trait TestUser {
  val name: String
  val email: Option[String]
  val password: Option[String]
}
