package grails.plugin.viewtools

import org.apache.commons.lang.Validate
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader

/**
 * ConfigKeyAppResourceLoader provides ability to load resources from a directory configured as app resource location.
 */
class ConfigKeyAppResourceLoader implements ResourceLoader  {

    /**
     * Config key for app resource directory which holds the resources. eg views.location
     */
    String baseAppResourceKey

    AppResourceService appResourceService

    void setBaseAppResourceKey(String key) {
        Validate.notEmpty(key)
        baseAppResourceKey = "config:" + key
    }

    @Override
    Resource getResource(String uri) {
        return appResourceService.getResourceRelative(baseAppResourceKey, uri)
    }

    @Override
    ClassLoader getClassLoader() {
        return null
    }

}
