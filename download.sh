#!/bin/sh

if [ ! -f finefoods.txt ]
then
  echo "Downloading reviews from https://snap.stanford.edu/data/web-FineFoods.html"
  wget https://snap.stanford.edu/data/finefoods.txt.gz
  gunzip finefoods.txt.gz
fi

# Convert file using this format:
# product/productId: B001E4KFG0
# review/userId: A3SGXH7AUHU8GW
# review/profileName: delmartian
# review/helpfulness: 1/1
# review/score: 5.0
# review/time: 1303862400
# review/summary: Good Quality Dog Food
# review/text: I have bought several of the Vitality canned dog food products and have found them all to be of good quality. The product looks more like a stew than a processed meat and it smells better. My Labrador is finicky and she appreciates this product better than  most.
#
# to:
#
# userId productId score
# A3SGXH7AUHU8GW,B001E4KFG0,5.0

cat finefoods.txt | grep -e "product/productId\|review/userId\|review/score" | sed -e 's/.*: //' | while read line1
do
  read line2
  read line3
  echo $line1,$line2,$line3
done | awk -F, '{print $2","$1","$3}' | sort > ratings.csv
