#!/bin/bash
ID_TO_SEARCH_FOR=$1
if [[ -z $1 ]] ; then 
	echo Defaulting to all items...
	ID_TO_SEARCH_FOR=-1
fi

curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:8080/ap/basic?idToSearchFor=${ID_TO_SEARCH_FOR}    | grep "message" | sed s/\"message\"\ \:\ \"//
