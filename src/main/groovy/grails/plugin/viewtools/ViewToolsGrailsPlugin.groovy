package grails.plugin.viewtools
import grails.plugins.*

class ViewToolsGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.1 > *"
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
Also GrailsWebEnvironment for binding a mock request is one doesnt exist so that services can operate without a controller
Used in freemarker and new jasper-spring.
'''

    // URL to the plugin's documentation
    def documentation = "https://github.com/9ci/grails-view-tools"
    def license = "APACHE"
    def organization = [ name: "9ci Inc", url: "http://www.9ci.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]


    def issueManagement = [ system: "github", url: "https://github.com/9ci/grails-view-tools/issues" ]
    def scm = [ url: "https://github.com/9ci/grails-view-tools" ]


}
