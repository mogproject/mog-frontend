#!/bin/bash

set -e
[[ $# -ne 1 ]] && exit 1

convert -extract 43x48+43+94 $1 OU.png
convert -extract 43x48+86+46 $1 HI.png
convert -extract 43x48+129+46 $1 KA.png
convert -extract 43x48+172+46 $1 KI.png
convert -extract 43x48+215+46 $1 GI.png
convert -extract 43x48+258+46 $1 KE.png
convert -extract 43x48+301+46 $1 KY.png
convert -extract 43x48+344+46 $1 FU.png
convert -extract 43x48+86+94 $1 RY.png
convert -extract 43x48+129+94 $1 UM.png
convert -extract 43x48+215+94 $1 NG.png
convert -extract 43x48+258+94 $1 NK.png
convert -extract 43x48+301+94 $1 NY.png
convert -extract 43x48+344+94 $1 TO.png

