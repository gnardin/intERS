#!/bin/bash

FILES=`find . -type f -name "c*.csv"`

for filename in $FILES
do
  `sed -i 's/LL;/0;/;s/LH;/1;/;s/HL;/2;/;s/HH;/3;/;s/TT/T0/;s/FLL/F0/;s/FLH/F1/;s/FHL/F2/;s/FHH/F3/;s/ILL/I0/;s/ILH/I1/;s/IHL/I2/;s/IHH/I3/' $filename`
done
