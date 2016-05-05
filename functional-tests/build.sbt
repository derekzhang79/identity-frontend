name := """functional-tests"""

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.0",
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.seleniumhq.selenium" % "selenium-java" % "2.52.0" % "test",
  "com.gu" %% "identity-test-users" % "0.5",
  "com.typesafe.play" %% "play-json" % "2.4.4",
  "org.scalatestplus" %% "play" % "1.4.0-M3" % "test",
  ws,
  "com.sun.mail" % "javax.mail" % "1.5.5"
)
