#!/bin/sh

if [ ! -f finefoods.txt ]
then
  echo "Downloading reviews from https://snap.stanford.edu/data/web-FineFoods.html"
  wget https://snap.stanford.edu/data/finefoods.txt.gz
  gunzip finefoods.txt.gz
fi

if [ -f urls.txt.gz ]
then
  echo "You already have the full urls downloaded"
  exit 0
fi

echo "Getting list of full URLs from Amazon. This can take a few hours..."
grep productId finefoods.txt | sed -e 's/.* //' | sort | uniq > ids.txt

while read id
do
    curl -i http://www.amazon.com/dp/$id | grep Location: | sed -e 's/.* //' 2> /dev/null
done < ids.txt > urls.txt

gzip urls.txt
