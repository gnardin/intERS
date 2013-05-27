#!/bin/bash

filenames=`ls -m1 /data/workspace/repast/intERS/output`

for f in $filenames
do
  echo "STARTED PROCESSING $f AT $(date)"
#  `/usr/bin/Rscript analysis-heterogeneous_vtest.R /data/workspace/repast/intERS/output/$f`
  echo "FINISHED PROCESSING $f AT $(date)"
done
