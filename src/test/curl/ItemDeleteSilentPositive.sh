#!/bin/bash
curl -o $1 -s -d '{ "updatedRows" : [  { "summaryId" : "W-101", "description" : "Blue Wagon", "unitCost" : "1.0", "sourcing" : "PUR", "activityState" : "DELETE_SILENT" } ] }' -H "Content-Type: application/json" -X POST http://localhost:8080/item/crud
