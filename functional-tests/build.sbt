name := """functional-tests"""

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe"            %   "config"                % "1.3.0",
  "ch.qos.logback"          %   "logback-classic"       % "1.1.3",
  "org.scalatest"           %%  "scalatest"             % "3.0.1",
  "org.seleniumhq.selenium" %   "selenium-java"         % "3.0.1",
  "org.seleniumhq.selenium" %   "htmlunit-driver"       % "2.26",
  "io.github.bonigarcia"    %   "webdrivermanager"      % "1.6.2",
  "com.gu"                  %%  "identity-test-users"   % "0.5"
)
