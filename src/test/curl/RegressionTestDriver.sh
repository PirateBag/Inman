#!/bin/bash

declare -a tests=( "ItemDeleteSilentPositive" "BomRecursionCheckPositive" "BomRecursionCheckNegative" "ItemExplosionReport" "ItemPickList" "ItemPickListForBom" "ItemPickListForOne" )

#Insert an item, update it and then delete it.
tests+=( "ItemInsertPositive" "ItemChangePositive" "ItemDeletePositive")

declare -i passed=0
declare -i failed=0
declare -i newBaseLines=0

#    .---------- constant part!
#    vvvv vvvv-- the code from above
RED='\033[0;31m'
NC='\033[0m' # No Color

# shellcheck disable=SC2068
for test in ${tests[@]}
do
  ./$test.sh $test.actual
  if [ ! -f $test.expected ]; then
    echo File $test.expected is being created from actual.
    mv $test.actual $test.expected
    newBaseLines+=1
  else
    if  cmp --silent "$test.actual" "$test.expected" ; then
      printf  '%-30s %-10s' "$test" "passed"
      echo
      passed+=1
    else
      printf  '%-30s %-10s' "$test" "failed"
      echo
      echo -e "${RED}diff $test.actual $test.expected${NC}"
      echo   ./$test.sh $test.actual; cat $test.actual
      failed+=1
    fi
  fi
done

echo --------------------
echo Summary of Test Results.
echo Passed $passed
echo Failed $failed
echo New Baselines $newBaseLines
