import sbt._

object IdentityFrontend extends Build {

  lazy val root = Project(id = "identity-frontend",
    base = file(".")) aggregate(functionalTests)

  lazy val functionalTests =
    Project(id = "functional-tests", base = file("functional-tests"))

}
