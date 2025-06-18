#!/bin/bash
declare TestResultFormat='%-35s %-10s\n'
declare TestSummaryFormat='%-15s %10s\n'
declare stopTesting="stopTesting"

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
  "ItemExplosionForShortBom;itemReport/explosion"

  #See if Item W-001 can be added as a child to W-002
  "BomRecursionCheckPositive;itemReport/bomRecursionCheck"
  "BomRecursionCheckNegative;itemReport/bomRecursionCheck"

  #Given the bill of materials of w-001->W-002->W-003, find legal items for a new
  # child inserted into W-002
  "ItemPickListForBom;itemPick/itemsForBom"

  # Insert a new item W-101.
  "ItemInsertPositive;item/crud"
  # Change W-101.
  "ItemChangePositive;item/crud"

  # Report all items...
  "ItemReportWithW-101;itemReport/showAllItems"

  # Remove the new item, and re-run original report.
  "ItemDeletePositive;item/crud"
  "IERafterDelete;itemReport/showAllItems"

  # Add components W-004 and W-005 to W-003
  "BomCrudTx3Add;bom/crud"

  # Adjust maxdepth for newly inserted items.
  "MaxDepthFor003;itemReport/whereUsedReport"

  # Rerun the item detail report to review new depth settings for 003, 004, and 005.
  "ItemExplosionReportFor003thru005;itemReport/showAllItems"

  # Reclaculate max depth from the, W-013..
  # There should be no change from the Max Depth calculation on 003
  "MaxDepthFor014;itemReport/whereUsedReport"
  "ItemExplosionReportFor003thru005;itemReport/showAllItems"

#Add the missing bill of material for W-001
  "BomCrudAddMissing;bom/crud"
  "ItemExplosionComplete;itemReport/explosion"

  # Generate an empty order line item report.
  "oliEmpty;oli/showAll"

# Insert orders for W-001 and W-002.
  "oliInsertOnly;oli/crud"

#Verify the items were inserted.
  "oliAfter001and002;oli/showAll"

  #Delete order 1 (W-001).
  "oliDelete;oli/crud"

#Make sure W-001 is really dead...
  "oliAfter001Delete;oli/showAll"

#Change the quantity ordered of order2 to 5.
  "oliCrudChange002;oli/crud"
   )
#   "stopTesting" \
declare -i passed=0
declare -i failed=0
declare -i newBaseLines=0

declare curlDriver=./_curlDriver.sh
declare testToRequest=./_testToRequest.sh

#    .---------- constant part!
#    vvvv vvvv-- the code from above
RED='\033[0;31m'
NC='\033[0m' # No Color


if [ ! -z "$1" ]; then
  echo Parameter passed
  tests=( $1 )
fi

#Convert any test file to a request file.  The remainder of the script
#only deals with .request.
for filename in *.test; do
  $testToRequest "$filename"
done

#Is there a server?
serverHealthCheck_root="ServerHealthCheck"
${curlDriver} $serverHealthCheck_root status

if [ ! -f $serverHealthCheck_root.actual  ]; then
  echo No response from server in $serverHealthCheck_root.actual file
  exit
else
   if  cmp --silent "$serverHealthCheck_root.actual" "$serverHealthCheck_root.expected" ; then
     printf "$TestResultFormat" "ServerHealthCheck"  "passed"
     passed+=1
  else
     printf "$TestResultFormat" "ServerHealthCheck"  "Failed"
     echo -e "${RED}diff $serverHealthCheck_root.actual $serverHealthCheck_root.expected${NC}"
     failed+=1
  fi
fi

rm *.actual

for test in ${tests[@]}
do
  IFS=';'
  read -ra ColumnsOfTest <<< "$test"
  testName=${ColumnsOfTest[0]}
  testService=${ColumnsOfTest[1]}

  #echo ${curlDriver} ${testName} ${testService}

  if [[ "$testName" == "$stopTesting" ]]; then
      break;
  fi

  ${curlDriver} ${testName} ${testService}

  if [ ! -f $testName.expected ]; then
    echo File $testName.expected is being created from actual.
    mv $testName.actual $testName.expected
    newBaseLines+=1
  else
    if  cmp --silent "$testName.actual" "$testName.expected" ; then
      printf  $TestResultFormat "$testName" "passed"
      passed+=1
    else
      printf  $TestResultFormat "$test" "failed"
      echo ${curlDriver} ${testName} ${testService}
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
printf $TestSummaryFormat Passed $passed
printf $TestSummaryFormat failed $failed
printf $TestSummaryFormat 'New Baselines' $newBaseLines
