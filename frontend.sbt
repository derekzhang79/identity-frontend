/**
 * Config for building frontend (client-side) assets
 */

// Configure npm commands to build frontend assets
buildCommands in build in Assets := Seq(
  BuildCommand("npm run build-css -s", excludeFilter = Some("*.js" | "*.hbs")),
  BuildCommand("npm run build-js -s", includeFilter = Some("*.js"))
)

// Ensure frontend build task is a source generator task
sourceGenerators in Assets <+= build in Assets

managedSourceDirectories in Assets += (buildOutputDirectory in build in Assets).value

// Include handlebars views in resources for lookup on classpath
unmanagedResourceDirectories in Compile += (resourceDirectory in Assets).value
