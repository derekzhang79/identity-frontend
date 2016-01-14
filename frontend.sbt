
// Ensure frontend build task is a source generator task
sourceGenerators in Assets <+= build in Assets

managedSourceDirectories in Assets += (buildOutputDirectory in build in Assets).value

// Include handlebars views in resources for lookup on classpath
unmanagedResourceDirectories in Compile += (resourceDirectory in Assets).value
