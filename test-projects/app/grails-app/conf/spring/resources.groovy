import grails.util.Environment

beans = {
	viewResourceLocator(grails.plugin.viewtools.ViewResourceLocator) { bean ->
        //customviews sits in src/main/resources
        searchLocations = ["file:./view-templates",
        	"classpath:testAppViewToolsGrailsAppConf",
        	"classpath:pluginViewToolsSrcMainResources",
        	"classpath:pluginViewToolsGrailsAppConf",
        	"classpath:testAppViewToolsSrcMainResources"
        ]

        searchBinary = false

        //in dev mode there will be a groovyPageResourceLoader that helps find the views
        //this will use the project dir as the base path to search
        //if(Environment.isDevelopmentEnvironmentAvailable()){
        if(!application.warDeployed){
            developmentResourceLoader = ref('groovyPageResourceLoader')
        }

    }

    simpleViewResolver(foobar.SimpleViewResolver) { bean ->
        viewResourceLocator = ref("viewResourceLocator")
    }
}
