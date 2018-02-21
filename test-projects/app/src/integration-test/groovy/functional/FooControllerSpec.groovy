package functional

import geb.spock.GebReportingSpec
import grails.testing.mixin.integration.Integration

@Integration
class FooControllerSpec extends GebReportingSpec {

	def "sanity check"() {
		when:
		go "/"

		then:
		title == "Welcome to Grails"
		//assert contains("success")
		def html = driver.pageSource
		assert html.contains("success")
	}

	def "foo index"() {
		when:
		go "/foo"

		then:
		//assert contains("success")
		def html = driver.pageSource
		assert html.contains("success")
	}

	def "overridenExternalTemplatesPath"() {
		when:
		go "/foo/overridenExternalTemplatesPath"

		then:
		def html = driver.pageSource
		assert html.contains("should see this one from the external file path")
	}

	def "itsInFoobarPlugin"() {
		when:
		go "/foo/itsInFoobarPlugin"

		then:
		def html = driver.pageSource
		assert html.contains("itsInFoobarPlugin.ftl success")
	}

	def "fooPluginWithArgument"() {
		when:
		go "/foo/fooPluginWithArgument"

		then:
		def html = driver.pageSource
		assert html.contains("itsInFoobarPlugin.ftl success")
	}

	def "tags"() {
		when:
		go "/foo/tags"

		then:
		assert $("#index").text().contains("foo/index.hbr success")
		assert $("#fooPlugin-override").text().contains("fooBar/override.md success")
		assert $("#overridenExternalTemplatesPath").text().contains("should see this one from the external file path")
		assert $("#fooPlugin-index").text().contains("view: /fooPlugin/index.md")
	}
}
