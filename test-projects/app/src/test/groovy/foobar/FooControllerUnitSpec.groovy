package foobar

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class FooControllerUnitSpec extends Specification implements ControllerUnitTest<FooController> {

    void "test index"() {
        when:
        controller.index()

        then:"fix me"
        assert 1==1

    }
}
