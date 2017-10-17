[![Build Status](https://travis-ci.org/yakworks/view-tools.svg?branch=master)](https://travis-ci.org/yakworks/view-tools)


Note: Grails 3 sources are master and Grails 2 is in 2.5.x branch

Documentation 
-----

Guide: https://yakworks.github.io/view-tools/  
API: https://yakworks.github.io/view-tools/api/

**Running mkdocs locally**  
Docs are built with https://yakworks.github.io/mkdocs-material-components/
Run 
> ```pip install -r pip-requirements.txt```
And then ```mkdocs serve``` see the docs if you have troubles

**Publishing**  
Build are automatically published by travis. 
Snapshots are published from master branch, and releases are published from tags to BinTray.

If you want to publish artifacts from your local system.
 
Define following properties in ~/.gradle/gradle.properties

- bintrayUser
- bintrayKey
- artifactoryUsername
- artifactoryPassword

bintray credentials are used for **bintrayUpload** task. Artifactory credentials are used for publishing snapshots to 9ci artifactory.

**Using latests SNAPSHOT**
TODO



