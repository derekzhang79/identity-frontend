import WebKeys._
import com.typesafe.sbt.web.incremental
import com.typesafe.sbt.web.incremental._

val build = taskKey[Seq[File]]("Compiles Frontend assets using npm")

val buildOutputDirectory: SettingKey[File] = settingKey[File]("Output directory for generated sources from npm build task")

buildOutputDirectory in Assets := webTarget.value / "build"

build in Assets := {
  val log = streams.value.log

  val sourceDir = (resourceDirectory in Assets).value
  val targetDir = (buildOutputDirectory in Assets).value
  val sources = (sourceDir ** "*.*").get

  // use sbt-web compile incremental API to only build when needed
  val results = incremental.syncIncremental((streams in Assets).value.cacheDirectory / "npm-build", Seq("npm-build")) {
    ops =>
      ops.map { op =>
        log.info("Running npm run build")
        val startTime = System.currentTimeMillis

        val cmd = Process("npm run build -s", baseDirectory.value) !< log
        val targetFiles = (targetDir ** "*.*").get
        val result = {
          if (cmd == 0) OpSuccess(sources.toSet, targetFiles.toSet)
          else {
            log.error(s"Non-zero error code for `npm run build`: $cmd")
            OpFailure
          }
        }

        log.info(s"Finished npm build in ${System.currentTimeMillis - startTime}ms")

        op -> result
      }.toMap -> Set.empty

  }

  (results._1 ++ results._2).toSeq
}

build in Assets := (build in Assets).dependsOn(nodeModules in Assets).value

managedSourceDirectories in Assets += (buildOutputDirectory in Assets).value

sourceGenerators in Assets <+= build in Assets

// Include handlebars views in resources for lookup on classpath
unmanagedResourceDirectories in Compile += (resourceDirectory in Assets).value
