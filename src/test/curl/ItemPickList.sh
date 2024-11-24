#!/bin/bash
curl -d '{ "idToSearchFor" : "1" }' -H "Content-Type: application/json" -X POST http://localhost:8080/itemPick/all