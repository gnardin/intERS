#!/bin/bash

for v in 1 2 3 4 5 6 7 8 9 10
do
  for i in 2 3
  do
    echo $v $i
    `/usr/bin/Rscript analysis.R $v $i 2>&1`
  done
done
