#!/bin/bash

DOWNLOAD=wget
if type wget &>/dev/null; then
    DOWNLOAD=wget
elif type curl &>/dev/null; then
    DOWNLOAD=curl -O
else
    echo "You don't have curl or wget!"
    DOWNLOAD=echo
fi

while read URL; do
    FNAME=$(basename "$URL")
    if [ ! -f "$FNAME" ]; then
        $DOWNLOAD "$URL"
    fi
done <<!
https://autobahn.s3.amazonaws.com/android/autobahn-0.5.0.jar
http://repository.codehaus.org/org/codehaus/jackson/jackson-core-asl/1.9.7/jackson-core-asl-1.9.7.jar
http://repository.codehaus.org/org/codehaus/jackson/jackson-mapper-asl/1.9.7/jackson-mapper-asl-1.9.7.jar
!
