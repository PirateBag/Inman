#!/bin/bash
declare testFile=$1
declare fileSplitOn=JSON

if [  -z ${testFile} ]; then
  echo $0 Empty parameter.  Exiting.
  exit
fi

declare requestFile="$(basename -s .test $1)".request

if [ -f $testFile ]; then
  #echo ${testFile} and \"${requestFile}\"

  csplit --quiet $testFile  /${fileSplitOn}/ --prefix="${requestFile}"

#  Remove the first of the split file, we don't need the comments.
  rm -f "${requestFile}"00

#   Remove the split string from the second file and use the remainder for
#  for the JSON Request.
  #echo grep -v $fileSplitOn "${requestFile}"01 $requestFile
  grep -v $fileSplitOn "${requestFile}"01 >$requestFile
  rm "${requestFile}"01""
else
  echo Unable to find the alledged test file $testFile.
  exit
fi
