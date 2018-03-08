#!/bin/bash

npm run watch &
P1=$!
./start-frontend.sh &
P2=$!
wait $P1 $P2
