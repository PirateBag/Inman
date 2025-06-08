#!/bin/bash
 RequestFile=$1.request
 ResponseFile=$1.actual
 ServiceSuffix=$2

 if [ -f $RequestFile ]; then
  #echo curl -o $ResponseFile  --silent --data "@$RequestFile" -H "Content-Type: application/json" -X POST http://localhost:8080/$ServiceSuffix
  curl -o $ResponseFile  --silent --data "@$RequestFile" -H "Content-Type: application/json" -X POST http://localhost:8080/$ServiceSuffix

else
  echo "Unable to find or open ${RequestFile}." >ResponseFile
fi
