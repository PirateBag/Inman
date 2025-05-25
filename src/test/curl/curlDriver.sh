#!/bin/bash
# $1 request file name:
# $2 service name as in localhost:8080/$ServiceName
# Outputs $1.actual
 #curl -o $1.actual -s -d '{ "updatedRows" : [  { "summaryId" : "W-101", "description" : "Blue Wagon", "unitCost" : "1.0", "sourcing" : "PUR", "activityState" : "DELETE" } ] }' -H "Content-Type: application/json" -X POST http://localhost:8080/item/crud

 RequestFile=$1.request
 ResponseFile=$1.actual
 ServiceSuffix=$2

 #echo "No response.  Is the server alive?" > $ResponseFile

 if [ -f $RequestFile ]; then
  curl -o $ResponseFile  --silent --data "@$RequestFile" -H "Content-Type: application/json" -X POST http://localhost:8080/$ServiceSuffix
#  echo curl -o $ResponseFile  --silent --data "@$RequestFile" -H "Content-Type: application/json" -X POST http://localhost:8080/$ServiceSuffix
else
  echo "Unable to find or open ${RequestFile}." >ResponseFile
fi
