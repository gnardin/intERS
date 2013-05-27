#!/bin/bash

FILES=`find . -type f -name "*.csv"`

for filename in $FILES
do
  dname=`dirname $filename`
  fname=`basename $filename`
  nname=`echo $fname | sed -e 's/0./LL./;s/1./LH./;s/2./HL./;s/3./HH./'`
  mv $filename $dname/$nname
done
