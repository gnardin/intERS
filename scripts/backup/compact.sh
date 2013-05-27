#!/bin/bash

files=`ls -l | grep -i ^d | awk '{print $9}'`

for file in $files
do
  echo $file
  `tar cvzf $file.tgz $file`
done
