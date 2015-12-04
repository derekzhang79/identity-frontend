
import WebKeys._

val build = taskKey[Seq[File]]("Compiles Frontend assets using npm")

val buildOutputDirectory = settingKey[File]("Output directory for generated sources from npm build task")

buildOutputDirectory in Assets := webTarget.value / "build"

build in Assets := {
  val log = streams.value.log
  log.info("Running npm run build")
  val outDir = (buildOutputDirectory in Assets).value
  val cmd = Process("npm run build", baseDirectory.value) !< log
  if (cmd != 0) sys.error(s"Non-zero error code for `npm run build`: $cmd")
  (outDir ***).get
}

build in Assets := (build in Assets).dependsOn(nodeModules in Assets).value

managedSourceDirectories in Assets += (buildOutputDirectory in Assets).value

sourceGenerators in Assets <+= build in Assets

// Include handlebars views in resources for lookup on classpath
unmanagedResourceDirectories in Compile += (resourceDirectory in Assets).value
