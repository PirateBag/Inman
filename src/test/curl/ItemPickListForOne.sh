#!/bin/bash
curl -o $1 -s -d '{ "idToSearchFor" : "1" }' -H "Content-Type: application/json" -X POST http://localhost:8080/itemPick/itemsForBom