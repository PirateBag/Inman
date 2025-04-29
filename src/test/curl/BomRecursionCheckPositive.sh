#!/bin/bash
 curl -o $1 -s -d '{ "parentId" : "5", "childId" : "1"  }' -H "Content-Type: application/json" -X POST http://localhost:8080/itemReport/bomRecursionCheck
