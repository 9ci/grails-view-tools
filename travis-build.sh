#!/usr/bin/env bash

set -e

rm -rf ./build

echo "### Running build for branch $TRAVIS_BRANCH ###"
./gradlew clean check --stacktrace --no-daemon

if [[ -n $TRAVIS_TAG ]] || [[ $TRAVIS_BRANCH == 'master' && $TRAVIS_REPO_SLUG == "yakworks/view-tools" && $TRAVIS_PULL_REQUEST == 'false' ]]; then

    if [[ -n $TRAVIS_TAG ]]
    then
        echo "### publishing release to BinTray"
        ./gradlew view-tools:bintrayUpload --no-daemon
    else
         echo "### publishing snapshot"
        ./gradlew view-tools:publish --no-daemon
    fi

    if [[ $TRAVIS_BRANCH == 'master' ]]
    then
        echo "### publishing building docs"
        git config --global user.name "9cibot"
        git config --global user.email "9cibot@9ci.com"
        git config --global credential.helper "store --file=~/.git-credentials"
        echo "https://$GH_TOKEN:@github.com" > ~/.git-credentials
        git clone https://${GH_TOKEN}@github.com/yakworks/view-tools.git -b gh-pages gh-pages --single-branch > /dev/null

        python3 -m mkdocs build

        cd gh-pages
        cp -r ../site/. .
        git add .
        git commit -a -m "Update docs for Travis build: https://travis-ci.org/$TRAVIS_REPO_SLUG/builds/$TRAVIS_BUILD_ID"
        git push origin HEAD
        cd ..
        rm -rf gh-pages
    fi

else
  echo "Not a Tag or Not on master branch, not publishing"
  echo "TRAVIS_BRANCH: $TRAVIS_BRANCH"
  echo "TRAVIS_TAG: $TRAVIS_TAG"
  echo "TRAVIS_REPO_SLUG: $TRAVIS_REPO_SLUG"
  echo "TRAVIS_PULL_REQUEST: $TRAVIS_PULL_REQUEST"
fi