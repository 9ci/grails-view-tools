import grails.util.Environment

grails3SrcDirs = ["$basedir/src/main/groovy","$basedir/src/main/resources"]

eventTestPhaseStart = { phase ->
    if (!buildConfig.grails.useGrails3FolderLayout) return
    //binding.variables.each { println it.key} 
    //binding.grailsSettings.testSourceDir = new File("/Users/basejump/source/grails/freemarker/testxxx")
    //println binding.grailsSettings.testSourceDir
    // println "grailsAppName : $grailsAppName"
    // buildConfig.each{
    //     println it
    // }

    println "eventTestPhaseStart : $phase in $grailsAppName"
    String srcTestPath

    if("unit" == phase){
        copyDirClean("$basedir/src/test/groovy", "$basedir/test/unit")
    }
    else if("integration" == phase){
        copyDirClean("$basedir/src/integration-test/groovy", "$basedir/test/integration", ["functional/"])
    }
    else if("functional" == phase){
        copyDirClean("$basedir/src/integration-test/groovy/functional", "$basedir/test/functional/functional")
        ant.copy(file:"$basedir/src/integration-test/groovy/GebConfig.groovy" ,todir:g2TestDir,failonerror:false)
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
    if (buildConfig.grails.useGrails3FolderLayout){
        println "eventCompileStart: useGrails3FolderLayout = true, adding grails3SrcDirs"
        for (String path in grails3SrcDirs) {
            if (new File(path).exists()){
                println "adding $path"
                projectCompiler.srcDirectories << path
                //copyResources(path, buildSettings.resourcesDir)
            }
        }
    }
}

eventCompileEnd = { 
    copyResources(buildSettings.resourcesDir)
}


eventCreatePluginArchiveStart = { stagingDir ->
    if (!buildConfig.grails.useGrails3FolderLayout) return

    println "stagingDir $stagingDir"
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

eventRunAppStart = {
    println "eventRunAppStart"

}

eventCreateWarStart = { warName, stagingDir ->
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


//eventTestCompileStart = { type ->
//    println "Type :$type"
//    println projectTestCompiler.srcDirectories
//    //throw new RuntimeException("FUBAR")
//    for (String path in extraTestSrcDirs) {
//        projectTestCompiler.srcDirectories << path
//    }
//    println projectTestCompiler.srcDirectories
//    //copyResources buildSettings.resourcesDir
//}
//
//eventAllTestsStart = {
//    //throw new RuntimeException("FUBAR")
//    classLoader.addURL(new File("$basedir/src/test/groovy").toURL())
//}
//
////eventCreateWarStart = { warName, stagingDir ->
////    copyResources "$stagingDir/WEB-INF/classes"
////}
//
//private copyTestResources(destination) {
//    ant.copy(todir: destination,
//            failonerror: false,
//            preservelastmodified: true) {
//        for (String path in extraSrcDirs) {
//            fileset(dir: path) {
//                exclude(name: '*.groovy')
//                exclude(name: '*.java')
//            }
//        }
//    }
//}

// eventTestPhaseStart = { phase ->
//     println "eventTestPhaseStart : $phase"
//     String srcTestPath

//     if("unit" == phase){
//         //make sure directory exists
//         ant.mkdir(dir:"$basedir/test/unit")
//         def dest = new File("$basedir/test/unit")
//         copyUnitTests()
//         srcTestPath = "$basedir/src/test/groovy"
//     }
//     if("integration" == phase){
//         //make sure directory exists
//         ant.mkdir(dir:"$basedir/test/integration")
//         srcTestPath = "$basedir/src/integration-test/groovy"
//     }//copyIntTests()

//     File dest = new File(buildSettings.testClassesDir, phase)
//     File source = new File(srcTestPath)
//     if(source.exists()){
//         def type = new GrailsSpecTestType(phase,phase)
//         projectTestRunner.projectTestCompiler.compileTests(type, source, dest)
//     }
// }