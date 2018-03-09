#!/bin/bash

# TeamCity Build Step
# https://teamcity-aws.gutools.co.uk/admin/editBuildRunners.html?id=buildType:Identity_Frontend
#
# All these steps need to be in the same bash script otherwise 'sbt assets' (FrontendBuildPlugin.scala)
# is unable to see the appropriate node version used by nvm in TeamCity.
# FIXME: How to pass environment from one build step to another?

# Use .nvmrc to install appropriate node
export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && . "$NVM_DIR/nvm.sh"
nvm install
nvm clear-cache

# Install node dependencies
npm cache clean -f
npm install

# Test linting
./git-hooks/pre-push

# Build scala and frontend assets, and upload to riffraff
./sbt "test" "riffRaffUpload"
