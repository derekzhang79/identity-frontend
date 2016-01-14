import com.typesafe.sbt.web.Import._
import com.typesafe.sbt.web.Import.WebKeys._
import com.typesafe.sbt.web.incremental
import com.typesafe.sbt.web.incremental._
import sbt._
import Keys._

import scala.collection.mutable.ArrayBuffer
import scala.util.control.NoStackTrace

object FrontendBuildPlugin extends AutoPlugin {

  import autoImport._

  object autoImport {
    val build = taskKey[Seq[File]]("Compiles Frontend assets using npm")
    val buildOutputDirectory: SettingKey[File] = settingKey[File]("Output directory for generated sources from npm build task")

    case class BuildCommand(command: String, generatedFiles: () => Seq[File])
  }

  override lazy val projectSettings = inConfig(Assets) {
    Seq(
      build := buildAssetsTask.value,

      buildOutputDirectory in build := webTarget.value / "build"
    )
  }

  lazy val buildAssetsTask = Def.task {
    val log = streams.value.log

    val sourceDir = (resourceDirectory in Assets).value
    val targetDir = (buildOutputDirectory in build in Assets).value
    val sources = (sourceDir ** "*.*").get


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
        Left(FrontendBuildError(cmd.command, exitCode, out.map(_._2).mkString("\n")))
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

  }.dependsOn(nodeModules in Assets)
}

case class FrontendBuildError(
                       command: String,
                       exitCode: Int,
                       output: String)
  extends RuntimeException(s"Exit code $exitCode whilst executing '$command':\n$output")
  with FeedbackProvidedException
  with NoStackTrace
