#!/bin/bash
declare testFile=$1
declare requestFile=`basename $1 .test`.request
declare fileSplitOn=JSON

if [ -f $testFile ]; then
  csplit --quiet $testFile  /${fileSplitOn}/ --prefix="${requestFile}"

#  Remove the first of the split file, we don't need the comments.
  rm -f "${requestFile}"00

#   Remove the split string from the second file and use the remainder for
#  for the JSON Request.
  grep -v $fileSplitOn "${requestFile}"01 >$requestFile
  rm "${requestFile}"01
fi
