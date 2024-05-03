#!/bin/bash
set -e # fail fast
set -x
BASE_URL=https://kadai.azurewebsites.net/kadai

test 200 -eq "$(curl -sw "%{http_code}" -o /dev/null "$BASE_URL/docs/rest/rest-api.html")"
test 200 -eq "$(curl -sw "%{http_code}" -o /dev/null "$BASE_URL/docs/rest/simplehistory-rest-api.html")"
test 200 -eq "$(curl -sw "%{http_code}" -o /dev/null "$BASE_URL/docs/rest/routing-rest-api.html")"
for module in kadai-core kadai-spring; do
  test 200 -eq "$(curl -sw "%{http_code}" -o /dev/null "$BASE_URL/docs/java/$module/index.html")"
done
for module in kadai-cdi; do
  test 200 -eq "$(curl -sw "%{http_code}" -o /dev/null "$BASE_URL/docs/java/$module/io/kadai/common/internal/package-summary.html")"
done
