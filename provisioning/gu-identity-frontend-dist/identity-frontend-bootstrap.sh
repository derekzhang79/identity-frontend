#!/bin/bash

source /set-env.sh
installScriptFromBucket gu-identity-dist/$stagetag identity-bootstrap.sh
installScriptFromBucket gu-identity-dist/$stagetag logstash-setup.sh

aws s3 cp s3://gu-$apptag-dist/$stacktag/$stagetag/$apptag/$apptag.tgz /$apptag/$apptag.tar.gz
aws s3 cp s3://gu-$apptag-private/$stagetag/$apptag.conf /etc/gu/$apptag.conf

tar -xvzf /$apptag/$apptag.tar.gz -C /$apptag
cp /$apptag/$apptag/deploy/$apptag-upstart.conf /etc/init/$apptag.conf

chown -R $apptag /$apptag
sed -i "s/<STAGE>/$stagetag/g" /etc/init/$apptag.conf
