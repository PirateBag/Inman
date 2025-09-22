#!/bin/bash
curl --silent -i -H "Accept: application/json" -H "Content-Type: application/json"  http://localhost:8080/adjustment/reportAll?idToReport=-1  | ./_textFormatter.sh


