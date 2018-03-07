/**
 * Config for building frontend (client-side) assets
 */

// Configure npm commands to build frontend assets
buildCommands in build in Assets := Seq()

pipelineStages := Seq(digest)

// Ensure frontend build task is a source generator task
sourceGenerators in Assets <+= build in Assets

