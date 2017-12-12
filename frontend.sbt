/**
 * Config for building frontend (client-side) assets
 */

// Configure npm commands to build frontend assets
buildCommands in build in Assets := Seq(
  BuildCommand("build-css", "npm run build-css -s", excludeFilter = Some("*.js" | "*.js.map" | "*.hbs")),
  BuildCommand("build-snow-css", "npm run build-snow-css -s", excludeFilter = Some("*.js" | "*.js.map" | "*.hbs")),
  BuildCommand("build-js", "npm run build-js -s", includeFilter = Some("*.js" | "*.js.map"))
)

pipelineStages := Seq(digest)

// Ensure frontend build task is a source generator task
sourceGenerators in Assets <+= build in Assets

managedSourceDirectories in Assets += (buildOutputDirectory in build in Assets).value

// Include handlebars views in resources for lookup on classpath
unmanagedResourceDirectories in Compile += (resourceDirectory in Assets).value


// Clean out node_modules directory as part of a `clean` task
cleanFiles ++= (WebKeys.nodeModuleDirectories in Assets).value


clean := {
  val log = streams.value.log
  log.info("Cleaned target and node_modules - next compile may take a while")
  clean.value
}
