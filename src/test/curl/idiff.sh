#!/bin/bash
if [[ -z $1 ]]; then
  echo $0 idiff testnumber.
  exit
fi

expectedFile=$1*expected
actualFile=$1*actual

if [[ -z $2 ]]; then
diff $actualFile $expectedFile
else
  mv $actualFile $expectedFile
  echo Moved.
fi
