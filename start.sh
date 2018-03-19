#!/usr/bin/env bash

ant

# start the rmiregistry
"$JAVA_HOME/bin/rmiregistry" &


