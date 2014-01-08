#!/bin/bash

for en in 10 40 80
do
  for to in 10 40 80
  do
    for ex in 10 20 30 40 50
    do
      for pu in 20 30 40 50
      do
        echo $en $to $ex $pu
        `/usr/bin/Rscript $1 $en $to $ex $pu 2>&1`
      done
    done
  done
done
