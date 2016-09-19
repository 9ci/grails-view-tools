beans = {
	viewResourceLocator(grails.plugin.viewtools.ViewResourceLocator) { bean ->
        //customviews sits in src/main/resources
        searchLocations = ["test-filepath-templates",
        	"classpath:testAppViewToolsGrailsAppConf",
        	"classpath:pluginViewToolsSrcMainResources",
        	"classpath:pluginViewToolsGrailsAppConf",
        	"classpath:testAppViewToolsSrcMainResources"
        ]

        //in dev mode there will be a groovyPageResourceLoader that helps find the views
        if(!application.warDeployed){
            developmentResourceLoader = ref('groovyPageResourceLoader')
        }

    }
}
