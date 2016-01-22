import com.typesafe.sbt.web.Import._
import com.typesafe.sbt.web.Import.WebKeys._
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
    val buildCommands: SettingKey[Seq[BuildCommand]] = settingKey[Seq[BuildCommand]]("Commands to run as part of the asset build process")

    case class BuildCommand(
        id: String,
        command: String,
        includeFilter: Option[FileFilter] = None,
        excludeFilter: Option[FileFilter] = None,
        sourceDirectory: Option[File] = None
    )
  }

  // Default settings for the task
  override lazy val projectSettings = inConfig(Assets) {
    Seq(
      build := buildAssetsTask.value,

      includeFilter in build := "*.*",
      excludeFilter in build := NothingFilter,

      sourceDirectory in build := resourceDirectory.value,
      buildOutputDirectory in build := webTarget.value / "build",

      buildCommands in build := Seq.empty
    )
  }

  lazy val buildAssetsTask = Def.task {
    val log = streams.value.log

    val _includeFilter = (includeFilter in build in Assets).value
    val _excludeFilter = (excludeFilter in build in Assets).value

    val _sourceDirectory = (sourceDirectory in build in Assets).value
    val targetDir = (buildOutputDirectory in build in Assets).value

    val _baseDirectory = (baseDirectory in build in Assets).value

    val cacheDirectory = (streams in Assets).value.cacheDirectory / "npm-build"

    val commands = (buildCommands in build in Assets).value


    // use sbt-web compile incremental API to only build when needed
    val (products, errors) = syncIncremental(cacheDirectory, commands) { ops =>
      val results = ops.map(op => op -> runProcess(op, _baseDirectory, log))

      val opResults = results.map {
        case (op, Right(_)) => {
          val sourceDir = op.sourceDirectory.getOrElse(_sourceDirectory)
          val includeF = op.includeFilter.getOrElse(_includeFilter)
          val excludeF = op.excludeFilter.getOrElse(_excludeFilter)

          val filesRead = (sourceDir ** (includeF -- excludeF)).get
          val filesWritten = (targetDir ** (includeF -- excludeF)).get

          op -> OpSuccess(filesRead.toSet, filesWritten.toSet)
        }
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


  def runProcess(cmd: BuildCommand, baseDirectory: File, log: Logger): Either[FrontendBuildError, String] = {
    log.info(s"[${cmd.id}] Running `${cmd.command}`")
    val startTime = System.currentTimeMillis

    val processLog = CommandProcessLogger(cmd)

    val exitCode = Process(cmd.command, baseDirectory) !< processLog

    log.info(s"[${cmd.id}] Finished `${cmd.command}` in ${System.currentTimeMillis - startTime}ms")

    if (exitCode == 0) {
      processLog.sendToLogger(log)

      Right(processLog.stdOut)
    }
    else
      Left(FrontendBuildError(cmd.command, exitCode, processLog.mkString))
  }


  /**
   * Captures stdout and stderr from a process.
   */
  private case class CommandProcessLogger(cmd: BuildCommand) extends ProcessLogger {
    private val out = new ArrayBuffer[(Level.Value, String)]

    def info(output: => String) = out.append(Level.Info -> output)
    def error(error: => String) = out.append(Level.Error -> error)
    def buffer[T](f: => T): T = f

    /**
     * Send captured output to a sbt task stream logger.
     * stderr output from the process is output as WARN level.
     */
    def sendToLogger(log: Logger) =
      out.foreach {
        case (Level.Error, msg) => log.warn(s"[${cmd.id}] $msg")
        case (_, msg) => log.info(s"[${cmd.id}] $msg")
      }

    def stdOut =
      asString(out.filter(_._1 == Level.Info))

    def mkString =
      asString(out)

    private def asString(in: ArrayBuffer[(Level.Value, String)]) =
      in.map(_._2).mkString("\n")
  }
}


case class FrontendBuildError(
                       command: String,
                       exitCode: Int,
                       output: String)
  extends RuntimeException(s"Exit code $exitCode whilst executing '$command':\n$output")
  with FeedbackProvidedException
  with NoStackTrace
