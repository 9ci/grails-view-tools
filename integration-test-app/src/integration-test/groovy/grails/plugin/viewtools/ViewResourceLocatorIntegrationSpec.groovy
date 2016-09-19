package grails.plugin.viewtools

import grails.test.spock.IntegrationSpec
import org.springframework.core.io.Resource
import test.DemoController

class ViewResourceLocatorIntegrationSpec extends IntegrationSpec {

	ViewResourceLocator viewResourceLocator
	DemoController controller

    def setup() {
        controller = new DemoController()
        controller.viewResourceLocator = viewResourceLocator
    }

    def cleanup() {
    }

    void "test normal grails-app/views/demo/demoView.md"() {
        //the classpath:customeviews should be setup in the bean
        when:
        Resource res = viewResourceLocator.locate('demo/demoView.md')

        then:
        assert res
        res.getURI().toString().endsWith("demo/demoView.md")

    }

    void "test conf/customviews/customview.hbr"() {
        //the classpath:customeviews should be setup in the bean
        when:
        Resource res = viewResourceLocator.locate('testAppViewToolsGrailsAppConf.hbr')

        then:
        assert res
        res.getURI().toString().endsWith( "testAppViewToolsGrailsAppConf.hbr")

    }

    void "test demo/override.md.ftl in file path"() {
        //the classpath:customeviews should be setup in the bean
        when:
        Resource res = viewResourceLocator.locate('demo/override.md.ftl')

        then:
        assert res
        res.getURI().toString().endsWith( "test-filepath-templates/demo/override.md.ftl")

    }

    void "find from inside controller"() {
        //the classpath:customeviews should be setup in the bean
        expect:
        controller.demoView()

    }

    void "find from view in plugin"() {
        //the classpath:customeviews should be setup in the bean
        expect:
        controller.inplugin()

    }

    void "other items in classpath"() {
        //the classpath:customeviews should be setup in the bean
        expect:
            ["pluginViewToolsSrcMainResources.hbr",
            "pluginViewToolsGrailsAppConf.hbr",
            "testAppViewToolsSrcMainResources.hbr"].each{
                assert viewResourceLocator.locate(it)
            }

    }
}
