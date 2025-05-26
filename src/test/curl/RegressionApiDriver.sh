#!/bin/bash

declare stopTesing="stopTesting"
declare -a tests=(
  "ClearAllData;clearAllData" \
  "ItemBatchAdd;item/crud" \
  "ItemPickList;itemPick/all"\
  "ItemExplosionReport;itemReport/showAllItems" \

  #Insert W-001 W-002,
  #       W-002 W-014
  "BomCrudTx1Add;bom/crud"

   # Insert W-001 and W-002 (Duplicate Key) and make sure the insert of
   # W-002 and W-0013 failes and is rolled back.
   "BomCrudTx2Add;bom/crud"

   #Verify that Only W-001,002 and W-002 and W-004 are in the database
  "ItemExplosionForShortBom;itemReport/explosion" \

  #See if Item W-001 can be added as a child to W-002
  "BomRecursionCheckPositive;itemReport/bomRecursionCheck" \

  "BomRecursionCheckNegative;itemReport/bomRecursionCheck" \

  "ItemPickListForBom;itemPick/itemsForBom"
  "ItemInsertPositive;item/crud"
  "ItemChangePositive;item/crud"
  "ItemDeletePositive;item/crud"
   )
#   "stopTesting" \
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

#Is there a server?
serverHealthCheck_root="ServerHealthCheck"
${curlDriver} $serverHealthCheck_root status
if [ ! -f $serverHealthCheck_root.actual  ]; then
  echo No response from server in $serverHealthCheck_root.actual file
  exit
else
   if  cmp --silent "$serverHealthCheck_root.actual" "$serverHealthCheck_root.expected" ; then
     echo Server Health Check passed.
  else
      echo Did not get expected health check resposne.
      cat $serverHealthCheck_root.actual
  fi
fi

for test in ${tests[@]}
do
  IFS=';'
  read -ra ColumnsOfTest <<< "$test"
  testName=${ColumnsOfTest[0]}
  testService=${ColumnsOfTest[1]}

  echo ${curlDriver} ${testName} ${testService}
  if [[ "$testName" == "$stopTesing" ]]; then
      break;
  fi

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
