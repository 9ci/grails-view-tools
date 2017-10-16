# Introduction
This document describes the release process designed and implemented for `grails view tools plugin`. Its main purpose is to explain to developers and maintainers how to prepare and release a new version of this plugin.

## The Process
We publish snapshots and releases for the plugin.
Snapshots are published from master branch. The artifacts are released to 9ci artifactory using ```gradle publish``` command.
 
Releases are published to BinTray from tags, every time a tag is pushed.

Release process is automated. 
Snapshots and releases are automatically published by travis build.
