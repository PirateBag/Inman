#!/bin/bash
if [[ -z $1 ]]; then
  echo $0 null or empty parameter.
  exit
fi

declare testToRequest=./_testToRequest.sh
RequestFile=$1.request
ResponseFile=$1.actual
TestFile=$1.test
ServiceSuffix=$2
#echo bag me.
$testToRequest "${TestFile}"
if [ -f $RequestFile ]; then
  #echo no, do me.
  #echo curl -o $ResponseFile  --data "@$RequestFile" -H "Content-Type: application/json" -X POST http://localhost:8080/$ServiceSuffix

  curl --silent -o /dev/null http://localhost:8080/toLog?testName=${1}
  curl -o $ResponseFile  --silent --data "@$RequestFile" -H "Content-Type: application/json" -X POST http://localhost:8080/$ServiceSuffix
else
  echo "Unable to find or open ${RequestFile}." | tee ResponseFile
fi
