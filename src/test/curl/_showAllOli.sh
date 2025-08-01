#!/bin/bash
curl --silent -i -H "Accept: application/json" -H "Content-Type: application/json"  http://localhost:8080/oli/showAll  | ./_textFormatter.sh


