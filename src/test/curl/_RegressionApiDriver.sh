#!/bin/bash
declare TestResultFormat='%-35s %-10s\n'
declare TestSummaryFormat='%-15s %10s\n'
declare stopTesting="stopTesting"

declare -a tests=(
  "0100_ClearAllData;clearAllData"

  #Add Items and verify with reports on picklist and all items.
  "0201_ItemCrudAdd;item/crud"
  "0203_ItemPickList;itemPick/all"
  "0205_ItemExplosionReport;itemReport/showAllItems"

#  Add some BOM line items to W-001
#  Test that transactions failed and rolled back.
  #Insert W-001 W-002,
  #       W-002 W-014
  "0301_BomCrudAdd;bom/crud"

   # Insert W-001 and W-002 (Duplicate Key) and make sure the insert of
   # W-002 and W-0013 failes and is rolled back.
   "0303_BomCrudAdd;bom/crud"

   #Verify that Only W-001,002 and W-002 and W-004 are in the database
  "0305_ItemExplosionForShortBom;itemReport/explosion"

  #See if Item W-001 can be added as a child to W-002
  "0307_BomRecursionCheckPositive;itemReport/bomRecursionCheck"
  "0309_BomRecursionCheckNegative;itemReport/bomRecursionCheck"

  #Given the bill of materials of w-001->W-002->W-003, find legal items for a new
  # child inserted into W-002
  "0311_ItemPickListForBom;itemPick/itemsForBom"

#Test Create/Replace/Update on Items.
  # Insert a new item W-101.
  "0401_ItemInsertPositive;item/crud"
  # Change W-101.
  "0403_ItemChangePositive;item/crud"

  # Report all items...
  "0405_ItemReportWithW-101;itemReport/showAllItems"

  # Remove W-001 item, and re-run original report.
  "0407_ItemDeletePositive;item/crud"
  "0409_IERafterDelete;itemReport/showAllItems"

  # Add components W-004 and W-005 to W-003
  "0501_BomCrudAdd;bom/crud"

  # Adjust maxdepth for newly inserted items.
  "0503_MaxDepth;itemReport/whereUsedReport"

  # Rerun the item detail report to review new depth settings for 003, 004, and 005.
  "0505_IERfor003to005X;itemReport/showAllItems"

  # Reclaculate max depth from the, W-013..
  # There should be no change from the Max Depth calculation on 003
  "0507_MaxDepthFor014;itemReport/whereUsedReport"

  # Repeat 0505...
  "0509_IER;itemReport/showAllItems"

   #Add the missing bill of material for W-001
  "0601_BomCrudAddMissing;bom/crud"
  "0603_IERcomplete;itemReport/explosion"

  # Generate an empty order line item report.
  "0605_oliEmpty;oli/showAll"

# Insert orders for W-001 and W-002.
  "0607_oliInsert;oli/crud"

#Verify the items were inserted.
  "0609_oliAfter001and002;oli/showAll"

  #Delete order 1 (W-001).
  "0611_oliDelete;oli/crud"

  #Make sure W-001 is really dead...
  "0613_oliAfter001Delete;oli/showAll"

   #Change the quantity ordered of order2 to 5.
  "0615_oliCrudChange;oli/crud"
  "stopTesting"

  "0617_oliReport;oli/showAll"

  #Assign some components manually to an order for testing.
  #Order 2 for 5x W-002, assign an W-013 Wagon Body, and W-014 Red Paint.
  "0619_oliCrudChange;oli/crud"

  # Generate a recurse report on MO 2.
  "0621_oliReport;oli/showAll"

  # Create new order for item 1 in "open" state.
  "0701_oliCrud;oli/crud"

  # Should see the new order, the old order #2 without components
  # and the new order with components.
  "0703_oliReport;oli/showAll"

  #Verify that the MO explosion report returns a good error message when the
  #Order doesn't exist.
  "0705_oliReport;oli/showAll"

  # Try some illegal things:
#    Deleting order 6 which is open.
  "0707_oliCrud;oli/crud"
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
  echo Parameter passed $1
  tests=( $1 )
fi

#Convert any test file to a request file.  The remainder of the script
#only deals with .request.
echo Converting tests to requests...
for filename in *.test; do
  #echo ==== ${filename}
  $testToRequest "${filename}"
done

echo Performing Health Check...

#Is there a server?
declare serverHealthCheck_root="0000_ServerHealthCheck"
${curlDriver} $serverHealthCheck_root status

if [ ! -f $serverHealthCheck_root.actual  ]; then
  echo No response from server in $serverHealthCheck_root.actual file
  exit
else
   if  cmp --silent "$serverHealthCheck_root.actual" "$serverHealthCheck_root.expected" ; then
     printf "$TestResultFormat" "ServerHealthCheck"  "passed"
     passed+=1
     rm -f ${serverHealthCheck_root}.actual ${serverHealthCheck_root}.request
     # echo rm -f ${serverHealthCheck_root}.actual ${serverHealthCheck_root}.request
  else
     printf "$TestResultFormat" "ServerHealthCheck"  "Failed"
     echo -e "${RED}diff $serverHealthCheck_root.actual $serverHealthCheck_root.expected${NC}"
     failed+=1
  fi
fi

for test in ${tests[@]}
do
  IFS=';'
  read -ra ColumnsOfTest <<< "$test"
  testName=${ColumnsOfTest[0]}
  testService=${ColumnsOfTest[1]}

#  echo ${curlDriver} ${testName} ${testService}

  if [[ "$testName" == "${stopTesting}" ]]; then
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
      rm ${testName}.actual ${testName}.request
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
