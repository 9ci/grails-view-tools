#!/usr/bin/env bash

set -e

rm -rf ./build

echo "### Running Tests ###"
./gradlew clean check --stacktrace

function getVersion {
   PROPERTY_FILE=gradle.properties
   PROP_VALUE=`cat $PROPERTY_FILE | grep "projectVersion" | cut -d'=' -f2`
   echo $PROP_VALUE
}

VERSION=$(getVersion)

if [[ $TRAVIS_BRANCH == 'master' && $TRAVIS_REPO_SLUG == "yakworks/view-tools" && $TRAVIS_PULL_REQUEST == 'false' ]]; then
    if [[ "$VERSION" == *-SNAPSHOT ]]
    then
        echo "### publishing snapshot"
        ./gradlew view-tools:publish
    else
        echo "### publishing to BinTray"
        ./gradlew view-tools:bintrayUpload
    fi

else
  echo "Not on master branch, so not publishing"
  echo "TRAVIS_BRANCH: $TRAVIS_BRANCH"
  echo "TRAVIS_REPO_SLUG: $TRAVIS_REPO_SLUG"
  echo "TRAVIS_PULL_REQUEST: $TRAVIS_PULL_REQUEST"
fi