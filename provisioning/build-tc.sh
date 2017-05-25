#!/bin/bash

set -ex

rm -Rf target

mkdir target

cp provisioning/riff-raff.yaml target/

cp -R provisioning/dist target/identity-frontend-provisioning/
