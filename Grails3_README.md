## Grails2 project structure changes
The grails-view-tools has _Events.groovy that show how we can first put the project into a structure that more closely alligns with Grails 2.This will make it easier to merge changes up from grails2 branch to 3. moving src/groovy to src/main/groovy. and then tests. 
see the [scripts/Grails3Prep.groovy.](https://github.com/9ci/grails-view-tools/blob/master/scripts/Grails3Prep.groovy)

1. move the files as per the Grails3Prep.groovy
2. move as much out of the "XyzPlugin.groovy" class as possible. closures can be put in a utility class with static methods and then have the deligate passed to for setting up spring beans for example. Then changes to this file can be better merged
3. for pubished plugins that have test stuff, better to move them to a very minimal test-projects/app for integration testing so we don't polute the main plugin sources

## Grails3 upgrades

see http://docs.grails.org/latest/guide/upgrading.html#upgrading2x
and this excellent write up http://philip.yurchuk.com/software/upgrading-to-grails-3/
Don't pay attention to what they say in steps 2. we are upgrading the project/plugin in the same dir so we don't loose git history. These addendum steps also assume that 

1. create a stock project/plugin to be used to copy files into
2. create a copy of the current project to convert to refere back to and merge files
3. remove all empty dirs and dirs that are not needed
4. delete grails-app/conf, and web-app
5. remove application.properties and old gradle/grails files that are not needed in the root dir

copy from stock grails3 project files in
1. build.gradle, gradle.properties, gradlew, gradlew.bat, settings.gradle and /gradle/
2. grails-app/conf, grails-app/controllers/URLMappings.groovy and grails-app/init
3. the XXXPlugin.groovy file from src/main/groovy/xyz package. Rename it appropriately. 

merge and get it compiling
1. update source for packages org.codehaus.groovy.grails -> org.grails and org.codehaus.groovy.grails.commons.GrailsApplication -> rails.core.GrailsApplication
2. merge dependencies from backup buildConfig.groovy into gradle.groovy
3. merge any config.groovy into application.yml(groovy). also merge in any XXXDefaultCongfig.groovy if using the plugin-config plugin
4. copy your grails-app/conf/spring/resources.groovy back in if its being used
5. rename package in init
6. merge bootStrap.groovy into init/bootStrap if using it
7. merge in the XXXPlugin.groovy files.

Integration Tests
1. extend from Specification and add the @Integration annotation
2. create-integration-test in new Grails 3 project to see example
3. Use GrailsWebEnvironment.bindRequestIfNull(grailsApplication.mainContext,writer) in def setup() if needed. 





