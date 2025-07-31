#!/bin/bash
grep message | sed s/\"message\"\ \:// | sed s/\ \ \ \ \"//

#
#if [[ -z $1 ]]; then
#  echo $0 null or empty parameter.
#  exit
#fi
#
#declare testToRequest=./_testToRequest.sh
#RequestFile=$1.request
#ResponseFile=$1.actual
#TestFile=$1.test
#ServiceSuffix=$2