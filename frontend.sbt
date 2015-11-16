
import WebKeys._

val sass = taskKey[Seq[File]]("Compiles Sass files")

val sassOutputDirectory = settingKey[File]("Output directory for generated css from Sass task")

sassOutputDirectory := webTarget.value / "sass"

sass in Assets := {
  val log = streams.value.log
  log.info("Running npm run build-sass")
  val cmd = Process("npm run build-sass", baseDirectory.value) !< log
  if (cmd != 0) sys.error(s"Non-zero error code for `npm run build-sass`: $cmd")
  (sassOutputDirectory.value ** "*.css*").get
}

managedSourceDirectories in Assets += sassOutputDirectory.value

sourceGenerators in Assets <+= sass in Assets
