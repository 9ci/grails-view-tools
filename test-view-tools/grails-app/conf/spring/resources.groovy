beans = {
	viewResourceLocator(grails.plugin.viewtools.ViewResourceLocator) { bean ->
        //customviews sits in conf
        searchLocations = ["test/resources","classpath:customviews"]

        //in dev mode there will be a groovyPageResourceLoader that helps find the views
        if(!application.warDeployed){
            developmentResourceLoader = ref('groovyPageResourceLoader')
        }

    }
}
