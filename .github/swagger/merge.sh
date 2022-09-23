#!/bin/bash

set -e

rm -f openapi-merge.json

scala_folder=$(find ./modules/api/target -type d | grep -E "/scala-[0-9\.]+/resource_managed/main$")
json_files=$(find "$scala_folder/" -type f -name "*.json")

for file in $json_files; do
    title=$(cat $file | jq -r '.info.title')
    echo "Merging $title"
    cat $file | jq --arg title "$title" -r '.paths[][] |= . + { tags: [$title] }' $file >$file.tmp.json
    rm $file
    mv $file.tmp.json $file
done

jsons=$(find "$scala_folder" -type f -name "*.json" -exec echo '{ "inputFile": "{}" }' \; | paste -d , -s -)
jq ".inputs += [$jsons]" .github/swagger/openapi-merge.json >>openapi-merge.json
npx openapi-merge-cli --config openapi-merge.json
