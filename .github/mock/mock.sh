
../swagger/merge.sh &&
docker run --rm -v "${PWD}:/local" openapitools/openapi-generator-cli generate \
    -i /local/output.swagger.json \
    -g openapi-yaml \
    -o /local/out/yaml && docker-compose up


