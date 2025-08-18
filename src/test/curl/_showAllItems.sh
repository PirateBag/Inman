#!/bin/bash
curl --silent -i -H "Accept: application/json" -H "Content-Type: application/json"  http://localhost:8080/itemReport/showAllItems?itemToFind=-1  | ./_textFormatter.sh


