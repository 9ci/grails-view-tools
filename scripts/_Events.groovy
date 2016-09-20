import grails.util.Environment

grails3SrcDirs = ["$basedir/src/main/groovy","$basedir/src/main/resources"]

eventTestPhaseStart = { phase ->
    if (!buildConfig.grails.useGrails3FolderLayout) return
    //binding.variables.each { println it.key} 
    //println "eventTestPhaseStart : $phase in $grailsAppName"
    String srcTestPath

    if("unit" == phase){
        copyDirClean("$basedir/src/test/groovy", "$basedir/test/unit")
    }
    else if("integration" == phase){
        copyDirClean("$basedir/src/integration-test/groovy", "$basedir/test/integration", ["functional/"])
    }
    else if("functional" == phase){
        copyDirClean("$basedir/src/integration-test/groovy/functional", "$basedir/test/functional/functional")
        ant.copy(file:"$basedir/src/integration-test/groovy/GebConfig.groovy" ,todir:"$basedir/test/functional",failonerror:false)
    }
}

eventTestPhaseEnd = { phase ->
    if (!buildConfig.grails.useGrails3FolderLayout) return
    
    if("unit" == phase){
        println "Clean up: DELETING $basedir/test/unit"
        ant.delete(dir:"$basedir/test/unit",failonerror:false)
    }
    else if("integration" == phase){
        println "Clean up: DELETING $basedir/test/integration"
        ant.delete(dir:"$basedir/test/integration",failonerror:false)
    }
    else if("functional" == phase){
        println "Clean up: DELETING $basedir/test/functional"
        ant.delete(dir:"$basedir/test/functional",failonerror:false)
    }
}

eventCompileStart = { x ->
    if (!buildConfig.grails.useGrails3FolderLayout) return
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

    //copy other resource files now too

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
    if (!buildConfig.grails.useGrails3FolderLayout) return 
    copyResources(buildSettings.resourcesDir)
}

eventCreatePluginArchiveStart = { stagingDir ->
    if (!buildConfig.grails.useGrails3FolderLayout) return

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

// eventRunAppStart = {
//     println "eventRunAppStart"

// }

eventCreateWarStart = { warName, stagingDir ->
    if (!buildConfig.grails.useGrails3FolderLayout) return
    copyResources("$stagingDir/WEB-INF/classes")
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
    println("copyResources($fromDir, $toDir)")
    ant.copy(todir: toDir, failonerror: false, preservelastmodified: true) {
        fileset(dir: fromDir) {
            exclude(name: '**/*.groovy')
            exclude(name: '**/*.java')
        }
    }
 }
