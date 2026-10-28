#!/bin/bash
set -a
source loadtest/.env.loadtest
set +a
./gradlew bootRun --args="--view-count.mode=$1"