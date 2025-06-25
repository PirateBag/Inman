#!/bin/bash

if [[ -z $1 ]]; then
  echo "No requestFile provided".
  exit
fi

if [[ -z $2 ]]; then
  echo "No file prefix provided"
  exit
fi

RequestFile=$1.request
ActualFile=$1.actual
TestFile=$1.test
ExpectedFile=$1.expected

Prefix=$2

MigratedTestFile=${Prefix}_${1}.test

Preamble="prefix.sh"
echo "Empty Test File" > $Preamble
echo "=== JSON ===" >>$Preamble

rm -f ${ActualFile}

if [ -f $TestFile ]; then
   echo $TestFile exists.  Renaming existing files.
   mv --no-clobber ${TestFile} ${Prefix}_${TestFile}
   mv --no-clobber ${ExpectedFile} ${Prefix}_${ExpectedFile}
else
   echo $TestFile does not exist, building test and renaming expected.
   cat  $Preamble ${RequestFile} > ${MigratedTestFile}
   mv --no-clobber ${ExpectedFile} ${Prefix}_${ExpectedFile}
fi
