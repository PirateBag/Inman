#!/bin/bash

declare -a tests=(
  "ItemDeleteSilentPositive;item/crud" \
  "BomRecursionCheckPositive;itemReport/bomRecursionCheck" \
  "BomRecursionCheckNegative;itemReport/bomRecursionCheck" \
  "ItemExplosionReport;itemReport/explosion" \
  "ItemPickList;itemPick/all"
  "ItemPickListForBom;itemPick/itemsForBom" \
  "ItemPickListForOne;itemPick/itemsForBom" \
  "ItemInsertPositive;item/crud" \
  "ItemChangePositive;item/crud" \
  "ItemDeletePositive;item/crud" )

declare -i passed=0
declare -i failed=0
declare -i newBaseLines=0

declare curlDriver=./curlDriver.sh

#    .---------- constant part!
#    vvvv vvvv-- the code from above
RED='\033[0;31m'
NC='\033[0m' # No Color

if [ ! -z "$1" ]; then
  echo Parameter passed
  tests=( $1 )
fi

# shellcheck disable=SC2068
for test in ${tests[@]}
do
  IFS=';'
  read -ra ColumnsOfTest <<< "$test"
  testName=${ColumnsOfTest[0]}
  testService=${ColumnsOfTest[1]}

#  echo ${curlDriver} ${testName} ${testService}
  ${curlDriver} ${testName} ${testService}

  if [ ! -f $testName.expected ]; then
    echo File $testName.expected is being created from actual.
    mv $testName.actual $testName.expected
    newBaseLines+=1
  else
    if  cmp --silent "$testName.actual" "$testName.expected" ; then
      printf  '%-30s %-10s' "$testName" "passed"
      echo
      passed+=1
    else
      printf  '%-30s %-10s' "$test" "failed"
      echo
      echo -e "${RED}diff $testName.actual $testName.expected${NC}"
      cat $testName.actual
      failed+=1

      if [ ! -z "$1" ]; then
        echo Parameter passed
        tests=( $1 )
        if [ $failed == "1" ] ; then
          cat $testName.expected
          diff $testName.actual $testName.expected
        fi
      fi
    fi
  fi
done

echo --------------------
echo Summary of Test Results.
echo Passed $passed
echo Failed $failed
echo New Baselines $newBaseLines
