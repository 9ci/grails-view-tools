class ViewToolsGrailsPlugin {
    // the plugin version
    def version = "0.3-grails2"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.3 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/",
        "grails-app/conf/pluginViewToolsGrailsAppConf/*",
        "src/groovy/pluginViewToolsSrcGroovy/*",
        "src/main/resources/pluginViewToolsSrcMainResources/"
    ]

    // TODO Fill in these fields
    def title = "View Tools Plugin" // Headline display name of the plugin
    def author = "Joshua Burnett"
    def authorEmail = "joshdev@9ci.com"
    def description = '''\
ViewResourceLocator for locating views in grails-app/views, plugins, and custom external paths.
Also GrailsWebEnvironment for binding a mock request is one doesn't exist so that services can operate without a controller.
'''

    // URL to the plugin's documentation
    def documentation = "https://github.com/9ci/grails-view-tools"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
    def organization = [ name: "9ci Inc", url: "http://www.9ci.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
    def issueManagement = [ system: "github", url: "https://github.com/9ci/grails-view-tools/issues" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://github.com/9ci/grails-view-tools" ]

    def watchedResources = [
            "file:./src/main/groovy/**/*.groovy",
            "file:./plugins/*/src/main/groovy/**/*.groovy"
    ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { ctx ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
