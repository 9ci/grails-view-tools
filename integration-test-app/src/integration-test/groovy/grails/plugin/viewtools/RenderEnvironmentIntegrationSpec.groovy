package grails.plugin.viewtools

import grails.test.spock.IntegrationSpec
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.servlet.WrappedResponseHolder
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder

class RenderEnvironmentIntegrationSpec extends IntegrationSpec {
    GrailsApplication grailsApplication

    def setup() {
    }

    def cleanup() {
    }

    void "test it already exists"() {
        expect:
        RequestAttributes grailsWebRequest = RequestContextHolder.getRequestAttributes()
        assert grailsWebRequest
    }

    void "test bindRequestIfNull"() {
        when: "null requests and response"
        RequestContextHolder.setRequestAttributes(null);
        WrappedResponseHolder.wrappedResponse = null
        then: "assert its null"
        RequestAttributes grailsWebRequest = RequestContextHolder.getRequestAttributes()
        assert !grailsWebRequest

        when: "bindRequestIfNull"
        Writer writer = new CharArrayWriter()
        RenderEnvironment.bindRequestIfNull(grailsApplication.mainContext, writer)

        then: "request and response should exist"
        assert RequestContextHolder.getRequestAttributes().request instanceof MockHttpServletRequest
        assert WrappedResponseHolder.wrappedResponse


    }

}
