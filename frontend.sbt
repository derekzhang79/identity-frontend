
import WebKeys._

val build = taskKey[Seq[File]]("Compiles Sass files")

val buildOutputDirectory = settingKey[File]("Output directory for generated sources from npm build task")

buildOutputDirectory := webTarget.value / "build"

build in Assets := {
  val log = streams.value.log
  log.info("Running npm run build")
  val cmd = Process("npm run build", baseDirectory.value) !< log
  if (cmd != 0) sys.error(s"Non-zero error code for `npm run build`: $cmd")
  (buildOutputDirectory.value ** "*.css*").get
}

build in Assets := (build in Assets).dependsOn(nodeModules in Assets).value

managedSourceDirectories in Assets += buildOutputDirectory.value

sourceGenerators in Assets <+= build in Assets
