import grails.util.Environment

grails3SrcDirs = ["$basedir/src/main/groovy","$basedir/src/main/resources"]

//no good way to add source paths to test in eventTestCompileStart and get them to run
//so just copy them into test/unit, etc.. so that grails can pick them up and run normally
eventTestPhaseStart = { phase ->
    if (!(buildConfig.grails?.useGrails3FolderLayout)) return
    //binding.variables.each { println it.key} 
    //println "eventTestPhaseStart : $phase in $grailsAppName"
    //String srcTestPath
    println "RUNNING TESTS $phase"

    if("unit" == phase){
        copyDirClean("$basedir/src/test/groovy", "$basedir/test/unit")
        ant.copy(todir:"$basedir/test/unit",failonerror:false) {
            fileset(dir: "$basedir/src/test/resources")
        }
    }
    else if("integration" == phase){
        //exclude anything in the functional classpath
        copyDirClean("$basedir/src/integration-test/groovy", "$basedir/test/integration", ["functional/"])
        ant.copy(todir:"$basedir/test/integration",failonerror:false) {
            fileset(dir: "$basedir/src/test/resources")
        }
    }
    else if("functional" == phase){
        copyDirClean("$basedir/src/integration-test/groovy/geb", "$basedir/test/functional/geb")
        ant.copy(todir:"$basedir/test/functional",failonerror:false) { fileset(dir: "$basedir/src/test/resources") }
        ant.copy(todir:"$basedir/test/functional/functional",failonerror:false) { fileset(dir: "$basedir/src/integration-test/groovy/functional") }
        ant.copy(file:"$basedir/src/integration-test/groovy/GebConfig.groovy" ,todir:"$basedir/test/geb",failonerror:false)
    }
}

eventTestPhaseEnd = { phase ->
    if (!(buildConfig.grails?.useGrails3FolderLayout)) return
    //remove the test dir that had the files copied into them
    if(phase in ["unit","integration","functional"]){
        println "Clean up: DELETING $basedir/test/$phase"
        ant.delete(dir:"$basedir/test/$phase",failonerror:false)
    }
}

eventCompileStart = { x ->
    if (!(buildConfig.grails?.useGrails3FolderLayout)) return
    //println "grails3 structure: eventCompileStart "

     //special for inline plugins add the source as its not moved from package
    for (pluginDir in projectCompiler.pluginSettings.getInlinePluginDirectories()) {
        
        ["${pluginDir}/src/main/groovy","${pluginDir}/src/main/resources"].each{

            def g3resource = new org.springframework.core.io.FileSystemResource(it)
            if (g3resource.exists()){
                println "eventCompileStart: inline-plugin found [$it] being adding to source path"
                projectCompiler.pluginSettings.compileScopePluginInfo.sourceDirectories  << g3resource
                //copyResources(it, buildSettings.resourcesDir)
            }
        }
    }

    //copy other resource files now too from the src directories

    println "eventCompileStart: useGrails3FolderLayout = true, adding grails3SrcDirs"
    for (String path in grails3SrcDirs) {
        if (new File(path).exists()){
            println "adding $path"
            projectCompiler.srcDirectories << path
            //copyResources(path, buildSettings.resourcesDir)
        }
    }
    
}

eventCompileEnd = {
    if (!(buildConfig.grails?.useGrails3FolderLayout)) return
    copyResources(buildSettings.resourcesDir)

}

eventCreatePluginArchiveStart = { stagingDir ->
    if (!(buildConfig.grails?.useGrails3FolderLayout)) return

    //println "stagingDir $stagingDir"
    ant.copy(todir:"$stagingDir/src/groovy",failonerror:false) {
        fileset(dir:"$stagingDir/src/main/groovy")
        fileset(dir:"$stagingDir/src/main/resources")
    }
    ant.delete(dir:"$stagingDir/src/main/groovy",failonerror:false)
    ant.delete(dir:"$stagingDir/src/main/resources",failonerror:false)
    ant.delete(dir:"$stagingDir/src/test",failonerror:false)
    ant.delete(dir:"$stagingDir/src/integration-test",failonerror:false)
    //throw new RuntimeException("FUBAR")
   // update staging directory contents here
}

eventCreatePluginArchiveEnd = { stagingDir ->
    if (!(buildConfig.grails?.useGrails3FolderLayout)) return
    cleanUpEmptyDirs()
}

eventRunAppStart= {
    //System.setProperty("grails.reload.enabled", "true")
    //println "eventRunAppStart: useGrails3FolderLayout = true, adding grails3SrcDirs"
//    for (String path in grails3SrcDirs) {
//        if (new File(path).exists()){
//            println "adding $path"
//            projectCompiler.srcDirectories << path
//            //copyResources(path, buildSettings.resourcesDir)
//        }
//    }
}

eventRunAppEnd = {
    if (!(buildConfig.grails?.useGrails3FolderLayout)) return
    cleanUpEmptyDirs()
}

eventCleanStart = { kind ->
    if (!(buildConfig.grails?.useGrails3FolderLayout)) return
    cleanUpEmptyDirs()
}

eventCreateWarStart = { warName, stagingDir ->
    if (!(buildConfig.grails?.useGrails3FolderLayout)) return
    copyResources("$stagingDir/WEB-INF/classes")
}

//after run-app or package plugin all the default dirs are created, clean up the noise
private cleanUpEmptyDirs(){
    println "deleting empty directories"
    deleteEmptyDirs("$basedir/grails-app/")
    deleteEmptyDirs("$basedir/src/java/")
    deleteEmptyDirs("$basedir/src/groovy/")
    deleteEmptyDirs("$basedir/lib/")
    deleteEmptyDirs("$basedir/test/")
    deleteEmptyDirs("$basedir/xxxx/")
}

//deletes all empty dirs for path including the path itself
private deleteEmptyDirs(path){
    ant.delete(includeemptydirs:true,quiet:true){
        fileset(dir:path){
            "and"{
                size(value:0)
                type(type:"dir")
            }
        }
    }
}

private copyDirClean(String fromDir, String toDir, excludes = []){
    ant.mkdir(dir:fromDir)
    ant.delete(dir:toDir,failonerror:false)
    ant.mkdir(dir:toDir)
    ant.copy(todir:toDir,failonerror:false) {
        fileset(dir:fromDir){
            excludes.each{
                exclude(name:it)
            }
        }
    }
}

private copyResources(toDir){
    //in place plugins
    for (pluginDir in projectCompiler.pluginSettings.getInlinePluginDirectories()) {
        ["${pluginDir}/src/main/groovy","${pluginDir}/src/main/resources"].each{
            copyResources(it, toDir)
        }
    }
    //new source directories
    for (String path in grails3SrcDirs) {
        copyResources(path, toDir)
    }
}

private copyResources(fromDir, toDir){
    //println("copyResources($fromDir, $toDir)")
    ant.copy(todir: toDir, failonerror: false, preservelastmodified: true) {
        fileset(dir: fromDir) {
            exclude(name: '**/*.groovy')
            exclude(name: '**/*.java')
        }
    }
 }
