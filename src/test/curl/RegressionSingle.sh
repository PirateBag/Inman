#!/bin/bash

declare -- test=$1

declare -a tests=( "ItemDeleteSilentPositive" "BomRecursionCheckPositive" "BomRecursionCheckNegative" "ItemExplosionReport" "ItemPickList" "ItemPickListForBom" "ItemPickListForOne" )

#Insert an item, update it and then delete it.
tests+=( "ItemInsertPositive" "ItemChangePositive" "ItemDeletePositive")

declare -i passed=0
declare -i failed=0
declare -i newBaseLines=0

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

echo
echo Diff Results
diff $test.actual $test.expected

echo
echo Actual output
cat $test.actual


