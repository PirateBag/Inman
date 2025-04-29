#!/bin/bash

declare -a tests=( "BomRecursionCheckPositive" "BomRecursionCheckNegative" "ItemExplosionReport" "ItemPickList" "ItemPickListForBom" "ItemPickListForOne")
declare -i passed=0
declare -i failed=0
declare -i newBaseLines=0

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
      echo test $test passed.
      passed+=1
    else
      echo Test $test failed.
      echo diff $test.actual $test.expected
      failed+=1
    fi
  fi
done

echo --------------------
echo Summary of Test Results.
echo Passed $passed
echo Failed $failed
echo New Baselines $newBaseLines
