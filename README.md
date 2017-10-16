[![Build Status](https://travis-ci.org/yakworks/view-tools.svg?branch=master)](https://travis-ci.org/yakworks/view-tools)


Note: Grails 3 sources are master and Grails 2 is in 2.5.x branch

Documentation 
-----

Guide: https://yakworks.github.io/view-tools/  
API: https://yakworks.github.io/view-tools/api/

**Running mkdocs locally**  
Ensure you have python installed

Run 
> ```pip install -r pip-requirements.txt```
And then ```mkdocs serve```

**Publishing**  
Define following properties in ~/.gradle/gradle.properties

- bintrayUser
- bintrayKey
- artifactoryUsername
- artifactoryPassword

bintray credentials are used for **bintrayUpload** task. Artifactory credentials are used for publishing snapshots to 9ci artifactory.



