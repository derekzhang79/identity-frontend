
// Configure npm commands to build frontend assets
buildCommands in build in Assets := Seq(
  BuildCommand("npm run build -s")
)

// Views not currently used in client-side assets
excludeFilter in build in Assets := "*.hbs"

// Ensure frontend build task is a source generator task
sourceGenerators in Assets <+= build in Assets

managedSourceDirectories in Assets += (buildOutputDirectory in build in Assets).value

// Include handlebars views in resources for lookup on classpath
unmanagedResourceDirectories in Compile += (resourceDirectory in Assets).value
