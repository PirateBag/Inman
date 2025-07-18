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
   # W-002 and W-0013 fails and is rolled back.
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

  # Re-calculate max depth from the, W-013..
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

  #Change order 6 from Open to Planned.  It should fail.
  "0709_oliCrud;oli/crud"

  # Change order 6 from open to closed
  # Components should also be closed.
  "0711_oliCrud;oli/crud"

#  Show all orders and look for order 6 to
#  be in closed state along with its children.

#    "message" : "   2    2       0   5.00   0.00  2025-0711  2025-0715 PLANNED   MO NONE"
#    "message" : "   3    3       2   5.00   0.00  2025-0711  2025-0712 PLANNED   MO NONE"
#    "message" : "   4    4       2   5.00   0.00  2025-0711  2025-0712 PLANNED   MO NONE"
#    "message" : "   5    5       4   5.00   0.00  2025-0704  2025-0708 PLANNED   MO NONE"
#    "message" : "   6    1       0  10.00   0.00  2025-0801  2025-0803 CLOSED   MO NONE"
#    "message" : "   7    2       6  10.00   0.00  2025-0801  2025-0803 CLOSED   MO NONE"
#    "message" : "   8    3       6  10.00   0.00  2025-0801  2025-0803 CLOSED   MO NONE"
#    "message" : "   9    4       6  10.00   0.00  2025-0801  2025-0803 CLOSED   MO NONE"
#    "message" : "  10    6       6  10.00   0.00  2025-0801  2025-0803 CLOSED   MO NONE"
#    "message" : "  11    7       6  80.00   0.00  2025-0801  2025-0803 CLOSED   MO NONE"
#    "message" : "  12    8       6  40.00   0.00  2025-0801  2025-0803 CLOSED   MO NONE"
#    "message" : "  13    9       6  40.00   0.00  2025-0801  2025-0803 CLOSED   MO NONE"

  "0713_oliReport;oli/showAll"

  #Delete those closed lines...
  "0715_oliCrud;oli/crud"

#  Order Ids 2 through 6 should remain...
  "0717_oliReport;oli/showAll"

  # Create two new planned orders with BOMs and delete all the old orders.
  "0719_oliCrud;oli/crud"
  "0721_oliReport;oli/showAll"

#
#  We now have the following orders:
#        14    2       0   7.00   0.00  2025-0801  2025-0811 PLANNED   MO NONE"
#        15    5       0   9.00   0.00  2025-0801  2025-0805 PLANNED   MO NONE"
#  Change order 14 to "OPEN and order 15 to Closed without ever opening it.  However,
#the 15 operation will failed because you can jump from planned to closed, rolling back the 14
#transaction.  No change in report output
#  "
  "0723_oliCrud;oli/crud"
  "0725_oliReport;oli/showAll"

#  Change order 14 to OPEN and 15 to open.
  "0727_oliCrud;oli/crud"
  "0729_oliReport;oli/showAll"

  # Verify the date setting logic in INSERT orders.
  "0731_oliCrud;oli/crud"

#####################  Automated Planning  ################
    # Remove just the orders and reset the id to 1.
    "0801_ClearOrders;clearAllData"

#    Create one orders for W-005 (which has one component, W-017)
    "0802_oliCrud;oli/crud"

    "0803_ap;ap/basic"

    "0804_oliCrud;oli/crud"

    "0805_ap;ap/basic"
#We have outstanding, open orders for W-005 and its single component W-017.
#         "   1    5       0     6.00   0.00  2025-0901  2025-0913 OPEN MOHEAD NONE"
#         "   3    5       0   500.00   0.00  2025-0911  2025-0915 OPEN MOHEAD NONE"
#         "   5    5       0  1000.00   0.00  2025-1011  2025-1015 OPEN MOHEAD NONE"
#         "   7    5       0  1500.00   0.00  2025-1111  2025-1115 OPEN MOHEAD NONE"
#         "   9    5       0    50.00   0.00  2025-1211  2025-1215 OPEN MOHEAD NONE"
#         "   2   17       1  1000.00   0.00  2025-0827  2025-0901 OPEN     PO NONE"
#         "   4   17       3   750.00   0.00  2025-0911  2025-0915 OPEN  MODET NONE"
#         "   6   17       5  1250.00   0.00  2025-1006  2025-1011 OPEN     PO NONE"
#         "   8   17       7  2250.00   0.00  2025-1106  2025-1111 OPEN     PO NONE"
#         "  10   17       9  1000.00   0.00  2025-1206  2025-1211 OPEN     PO NONE"

  "0806_oliCrud;oli/crud"
  "0807_oliReport;oli/showAll"
#
#       "  11    3       0   500.00   0.00  2025-0914  2025-0915 OPEN MOHEAD NONE"
#       "  16    3       0  1000.00   0.00  2025-1014  2025-1015 OPEN MOHEAD NONE"
#       "  21    3       0  1500.00   0.00  2025-1114  2025-1115 OPEN MOHEAD NONE"
#       "  26    3       0    50.00   0.00  2025-1214  2025-1215 OPEN MOHEAD NONE"
#       "   1    5       0     6.00   0.00  2025-0901  2025-0913 OPEN MOHEAD NONE"
#       "   3    5       0   500.00   0.00  2025-0911  2025-0915 OPEN MOHEAD NONE"
#       "  12    5      11   500.00   0.00  2025-0914  2025-0915 OPEN  MODET NONE"
#       "   5    5       0  1000.00   0.00  2025-1011  2025-1015 OPEN MOHEAD NONE"
#       "  17    5      16  1000.00   0.00  2025-1014  2025-1015 OPEN  MODET NONE"
#       "   7    5       0  1500.00   0.00  2025-1111  2025-1115 OPEN MOHEAD NONE"
#       "  22    5      21  1500.00   0.00  2025-1114  2025-1115 OPEN  MODET NONE"
#       "   9    5       0    50.00   0.00  2025-1211  2025-1215 OPEN MOHEAD NONE"
#       "  27    5      26    50.00   0.00  2025-1214  2025-1215 OPEN  MODET NONE"
#       "  13    9      11  1000.00   0.00  2025-0914  2025-0915 OPEN  MODET NONE"
#       "  18    9      16  2000.00   0.00  2025-1014  2025-1015 OPEN  MODET NONE"
#       "  23    9      21  3000.00   0.00  2025-1114  2025-1115 OPEN  MODET NONE"
#       "  28    9      26   100.00   0.00  2025-1214  2025-1215 OPEN  MODET NONE"
#       "  14   11      11  1000.00   0.00  2025-0914  2025-0915 OPEN  MODET NONE"
#       "  19   11      16  2000.00   0.00  2025-1014  2025-1015 OPEN  MODET NONE"
#       "  24   11      21  3000.00   0.00  2025-1114  2025-1115 OPEN  MODET NONE"
#       "  29   11      26   100.00   0.00  2025-1214  2025-1215 OPEN  MODET NONE"
#       "  15   12      11  1000.00   0.00  2025-0914  2025-0915 OPEN  MODET NONE"
#       "  20   12      16  2000.00   0.00  2025-1014  2025-1015 OPEN  MODET NONE"
#       "  25   12      21  3000.00   0.00  2025-1114  2025-1115 OPEN  MODET NONE"
#       "  30   12      26   100.00   0.00  2025-1214  2025-1215 OPEN  MODET NONE"
#       "   2   17       1  1000.00   0.00  2025-0827  2025-0901 OPEN     PO NONE"
#       "   4   17       3   750.00   0.00  2025-0911  2025-0915 OPEN  MODET NONE"
#       "   6   17       5  1250.00   0.00  2025-1006  2025-1011 OPEN     PO NONE"
#       "   8   17       7  2250.00   0.00  2025-1106  2025-1111 OPEN     PO NONE"
#       "  10   17       9  1000.00   0.00  2025-1206  2025-1211 OPEN     PO NONE"
    "0808_ap;ap/basic"
    "0809_ibp;ap/inventoryBalanceProjection"

#    Create one order for W-001, and look for all the cascade order creation.
    "0810_oliCrud;oli/crud"
    "0811_ap;ap/basic"
   )
#   "stopTesting" \
declare -i passed=0
declare -i failed=0
declare -i newBaseLines=0

declare curlDriver=./_curlDriver.sh
declare testToRequest=./_testToRequest.sh

#    .---------- constant part!
#    the code from above
RED='\033[0;31m'
NC='\033[0m' # No Color
if [ -n "$1" ]; then
  echo Parameter passed "$1"
  # shellcheck disable=SC2206
  tests=( ${1} )
fi

#Extract the requests from the test files...
#echo Converting tests to requests...
#for filename in *.test; do
#  #echo ==== ${filename}
#  $testToRequest "${filename}"
#done

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

  if [ ! -f "$testName".expected ]; then
    echo File "$testName".expected is being created from actual.
    mv "$testName".actual "$testName".expected
    newBaseLines+=1
  else
    if  cmp --silent "$testName.actual" "$testName.expected" ; then
      printf  "$TestResultFormat" "$testName" "passed"
      rm "${testName}".actual "${testName}".request
      passed+=1
    else
      printf  $TestResultFormat "$test" "failed"
      echo ${curlDriver} ${testName} ${testService}
      echo -e "${RED}diff $testName.actual $testName.expected${NC}"
      cat $testName.actual
      failed+=1

      if [ -n "$1" ]; then
        echo Parameter passed
        tests=( ${1} )
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
printf $TestSummaryFormat 'Passed' $passed
printf $TestSummaryFormat 'Failed'  $failed
printf $TestSummaryFormat 'New Baselines' $newBaseLines
