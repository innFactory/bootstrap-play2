#!/bin/bash

# Exit on any error
set -e

rp generate-kubernetes-resources eu.gcr.io/$PROJECT/$NAME:$CIRCLE_SHA1 \
  --generate-all \
  --ingress-annotation ingress.kubernetes.io/rewrite-target=/ \
  --ingress-annotation nginx.ingress.kubernetes.io/rewrite-target=/ \
  --service-type LoadBalancer \
  --pod-controller-replicas 2 \
  --env prod \
  --env JAVA_OPTS=""\
"-DDATABASE_DB=$PROD_POSTGRES_DB "\
"-DDATABASE_HOST=$PROD_POSTGRES_HOST "\
"-DDATABASE_USER=$PROD_POSTGRES_USER "\
"-DDATABASE_PORT=$PROD_POSTGRES_PORT "\
"-DDATABASE_PASSWORD=$PROD_POSTGRES_PASSWORD" | kubectl apply -f -