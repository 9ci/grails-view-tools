package foobar

import grails.plugin.viewtools.GrailsWebEnvironment
import grails.plugin.viewtools.ViewResourceLocator
import grails.test.mixin.integration.Integration
import spock.lang.*

@Integration
//@Rollback
class FooControllerSpec extends Specification  {

	ViewResourceLocator viewResourceLocator
	DemoController controller
    def grailsApplication

    StringWriter writer = new StringWriter()

    def setup() {
        GrailsWebEnvironment.bindRequestIfNull(grailsApplication.mainContext,writer)
        controller = new DemoController()
        controller.viewResourceLocator = viewResourceLocator
    }

//
//    void "simple fooGsp test"() {
//        expect:
//
//            def r = controller.fooGsp()
//            //assert r.view == "fooGsp.gsp"
//            //log.debug("!!!!!!!!!!!!!!!!writer : ${writer.toString()}")
//            println("!!!!!!!!!!!!!!!!modelAndView : ${controller.modelAndView}")
//            assert controller.modelAndView.viewName == "/demo/fooGsp.gsp"
//            println("!!!!!!!!!!!!!!!!writer : ${writer.toString()}")
//            assert writer
//    }
//
//    void "simple fooGspPlugin test"() {
//        expect:
//
//            def r = controller.fooGspPlugin()
//            //assert r.view == "fooGsp.gsp"
//            //log.debug("!!!!!!!!!!!!!!!!writer : ${writer.toString()}")
//            println("!!!!!!!!!!!!!!!!modelAndView : ${controller.modelAndView}")
//            assert controller.modelAndView.viewName == "/plugins/foobar-plugin-0.1/demo/fooGsp.gsp"
//            println("!!!!!!!!!!!!!!!!writer : ${writer.toString()}")
//            assert writer
//    }
//
//    void "fooFtl test"() {
//        expect:
//
//            def r = controller.fooFtl()
//            //assert r.view == "fooGsp.gsp"
//            //log.debug("!!!!!!!!!!!!!!!!writer : ${writer.toString()}")
//            println("!!!!!!!!!!!!!!!!modelAndView : ${controller.modelAndView}")
//            assert controller.modelAndView.viewName == "/demo/foo.ftl"
//            println("!!!!!!!!!!!!!!!!writer : ${writer.toString()}")
//            assert writer
//    }
//
//    void "fooFtl Pugin test"() {
//        expect:
//
//            def r = controller.fooFtlPlugin()
//            //assert r.view == "fooGsp.gsp"
//            //log.debug("!!!!!!!!!!!!!!!!writer : ${writer.toString()}")
//            println("!!!!!!!!!!!!!!!!modelAndView : ${controller.modelAndView}")
//            assert controller.modelAndView.viewName == "/plugins/foobar-plugin-0.1/demo/foo.ftl"
//            println("!!!!!!!!!!!!!!!!writer : ${writer.toString()}")
//            assert writer
//    }

}
