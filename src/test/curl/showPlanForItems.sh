#!/bin/bash
declare itemToSearchFor=$1

if [ -z "$itemToSearchFor" ]; then
	itemToSearchFor=-1
fi

curl --silent -i -H "Accept: application/json" -H "Content-Type: application/json"  http://localhost:8080/ap/inventoryBalanceProjection?idToSearchFor=${itemToSearchFor}  | tee temp | ./_textFormatter.sh


