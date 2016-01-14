import WebKeys._
import com.typesafe.sbt.web.incremental
import com.typesafe.sbt.web.incremental._

import scala.collection.mutable.ArrayBuffer
import scala.util.control.NoStackTrace


val build = taskKey[Seq[File]]("Compiles Frontend assets using npm")

val buildOutputDirectory: SettingKey[File] = settingKey[File]("Output directory for generated sources from npm build task")

buildOutputDirectory in Assets := webTarget.value / "build"

build in Assets := {
  val log = streams.value.log

  val sourceDir = (resourceDirectory in Assets).value
  val targetDir = (buildOutputDirectory in Assets).value
  val sources = (sourceDir ** "*.*").get

  case class BuildCommand(command: String, generatedFiles: () => Seq[File])
  case class BuildError(
      command: String,
      exitCode: Int,
      output: String)
    extends RuntimeException(s"Exit code $exitCode whilst executing '$command':\n$output")
    with FeedbackProvidedException
    with NoStackTrace

  val commands = Seq(
    BuildCommand("npm run build -s", () => (targetDir ** "*.*").get )
  )

  def runProcess(cmd: BuildCommand) = {
    log.info(s"Running ${cmd.command}")
    val startTime = System.currentTimeMillis

    val out = new ArrayBuffer[(String, String)]

    val logger = new ProcessLogger {
      def info(output: => String) = out.append("info" -> output)
      def error(error: => String) = out.append("error" -> error)
      def buffer[T](f: => T): T = f
    }

    val exitCode = Process(cmd.command, baseDirectory.value) !< logger

    log.info(s"Finished ${cmd.command} in ${System.currentTimeMillis - startTime}ms")

    if (exitCode == 0) {
      out.foreach {
        case ("error", msg) => log.warn(msg)
        case (_, msg) => log.info(msg)
      }
      Right()
    }
    else
      Left(BuildError(cmd.command, exitCode, out.map(_._2).mkString("\n")))
  }

  val cacheDirectory = (streams in Assets).value.cacheDirectory / "npm-build"

  // use sbt-web compile incremental API to only build when needed
  val (products, errors) = incremental.syncIncremental(cacheDirectory, commands) { ops =>
    val results = ops.map(op => op -> runProcess(op))

    val opResults = results.map {
      case (op, Right(_)) => op -> OpSuccess(sources.toSet, op.generatedFiles().toSet)
      case (op, Left(_)) => op -> OpFailure
    }.toMap

    val errors = results.collect {
      case (_, Left(e)) => e
    }

    (opResults, errors)
  }

  errors.headOption.map { error =>
    throw error
  }

  products.to[Seq]
}

build in Assets := (build in Assets).dependsOn(nodeModules in Assets).value

managedSourceDirectories in Assets += (buildOutputDirectory in Assets).value

sourceGenerators in Assets <+= build in Assets

// Include handlebars views in resources for lookup on classpath
unmanagedResourceDirectories in Compile += (resourceDirectory in Assets).value
