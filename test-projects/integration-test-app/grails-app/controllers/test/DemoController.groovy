package test

import grails.plugin.viewtools.ViewResourceLocator

class DemoController {

    ViewResourceLocator viewResourceLocator

    def demoView() { 
    	return viewResourceLocator.locate("demo/demoView.md")
    }

    def inplugin() { 
    	return viewResourceLocator.locate("demo/inplugin.xyz")
    }
    
}
