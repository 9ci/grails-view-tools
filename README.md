# grails-view-tools
utility to help locate views in the spring mvc context

setups a ViewResourceLocator bean tha can be used to help locate views in the spring mvc using a ViewResolver
Extends DefaultGroovyPageLocator because the handy methods we want are protected.

Used to locate View resources whether in development or WAR deployed mode from static
resources, custom resource loaders and binary plugins.
Loads from a local grails-app folder for dev and from WEB-INF in
development mode.

ContextResource locate(String uri) is the primary method and is used to find a view resource for a path. For example /foo/bar.xyz will search for /WEB-INF/grails-app/views/foo/bar.xyz in production and grails-app/views/foo/bar.xyz at development time. It also uses the the controller if called from a plugin to figure out where its located and finally does a brute force locate. Most of the logic is based on and uses what Grail's DefaultGroovyPageLocator does. 

see https://github.com/9ci/grails-view-tools/tree/master/test/test-view-tools/test/integration/grails/plugin/viewtools

and 
https://github.com/9ci/grails-view-tools/blob/master/src/groovy/grails/plugin/viewtools/ViewResourceLocator.groovy
for usage details
