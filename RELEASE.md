# Introduction
This document describes the release process designed and implemented for `grails view tools plugin`. Its main purpose is to explain to developers and maintainers how to prepare and release a new version of this plugin.

## The Process
We publish snapshots and releases for the plugin.
Snapshots are published from master branch. The artifacts are released to 9ci artifactory using ```gradle publish``` command.
 
Releases are published to BinTray from tags, every time a tag is pushed.

Release process is automated. 
Snapshots and releases are automatically published by travis build.

### Releasing a new snapshot
Snapshots gets published automatically from master branch. Just pushing any commits to master will trigger a travis build and new snapshot will get published.

### Publish a new release
New releaseses are published from tags. Follow the following steps to publish a new release.

- branch off from master
- increment version number (projectVersion) in gradle.properties file
- Make any other code changes if required.
- Push branch
- Create a tag, new releases will be automatically published to bintray by travis.
- If any code changes were made to the branch, merge back to master
- increment the snapshot version in master branch


