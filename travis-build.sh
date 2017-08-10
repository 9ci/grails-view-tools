#!/usr/bin/env bash

set -e

rm -rf ./build

echo "### Running plugin tests ###"
./gradlew clean check assemble --stacktrace

if [[ $TRAVIS_BRANCH == 'master' && $TRAVIS_REPO_SLUG == "9ci/grails-view-tools" && $TRAVIS_PULL_REQUEST == 'false' ]]; then
	echo "### publishing plugin Bintray"
	./gradlew bintrayUpload

else
  echo "Not on master branch, so not publishing"
  echo "TRAVIS_BRANCH: $TRAVIS_BRANCH"
  echo "TRAVIS_REPO_SLUG: $TRAVIS_REPO_SLUG"
  echo "TRAVIS_PULL_REQUEST: $TRAVIS_PULL_REQUEST"
fi