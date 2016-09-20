package grails.plugin.viewtools

import grails.util.GrailsNameUtils
import groovy.transform.CompileStatic
import groovy.util.logging.Commons
import org.codehaus.groovy.grails.commons.ControllerArtefactHandler
import org.codehaus.groovy.grails.core.io.DefaultResourceLocator
import org.codehaus.groovy.grails.core.io.ResourceLocator
import org.codehaus.groovy.grails.io.support.GrailsResourceUtils
import org.codehaus.groovy.grails.web.pages.discovery.DefaultGroovyPageLocator
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.core.io.ContextResource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.UrlResource
import org.springframework.util.ResourceUtils
import org.springframework.util.StringUtils

import javax.servlet.http.HttpServletRequest
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Extended because the handy methods we want in DefaultGroovyPageLocator are protected.
 *
 * Used to locate View resources whether in development or WAR deployed mode from static
 * resources, custom resource loaders and binary plugins.
 * Loads from a local grails-app folder for dev and from WEB-INF in
 * development mode.
 *
 * GroovyPageResourceLoader bean exists in dev/test mode and deals with plugin paths, inplace etc... will not be here in deplyed war
 * @see org.codehaus.groovy.grails.web.pages.GroovyPageResourceLoader
 * GroovyPagesGrailsPlugin is where the original beans are setup, take a look at the source to see how they are setup
 * @see org.codehaus.groovy.grails.plugins.web.GroovyPagesGrailsPlugin
 */
@Commons
@CompileStatic
class ViewResourceLocator extends DefaultGroovyPageLocator {

    /**
     * the search locations to try first
     */
    List<String> searchLocations = []

    public void setPreResourceLoaders(List<ResourceLoader> preLoaders) {
        Collection<ResourceLoader> newRl = new ConcurrentLinkedQueue<ResourceLoader>()
        newRl.addAll(preLoaders)
        newRl.addAll(resourceLoaders)
        resourceLoaders = newRl
    }

    /**
     * adds a dev loader to the list
     */
    public void setDevelopmentResourceLoader(ResourceLoader resourceLoader) {
        addResourceLoader(resourceLoader)
    }


    /**
     * Find a view resource for a path. For example /foo/bar.xyz will search for
     * /WEB-INF/grails-app/views/foo/bar.xyz in production
     * and grails-app/views/foo/bar.xyz at development time
     *
     * @param uri The uri to search
     * @return The ContextResource with the realtive path
     */
    ContextResource locate(String uri) {
        log.debug("locate is trying  [$uri]")
        Resource resource

        //if its something like "/file:..." then lop off the "/" prefix
        if( uri.startsWith('/') && ResourceUtils.isUrl(uri.substring(1)) ){
            uri = uri.substring(1)
        }
        //if it starts with jndi, file, etc... then just pass it through to the resource finders
        if(ResourceUtils.isUrl(uri)){
            log.debug("isUrl=true so trying  [$uri]")
            return (ContextResource)findResource([uri]) //FAST EXIT
        }

        log.debug("Trying searchLocations $searchLocations")
        List searchPaths = searchLocations.collect{ String path ->
            if(!path.endsWith("/")) {path = "$path/" }
            StringUtils.applyRelativePath(path, uri)
        }
        resource = findResource(searchPaths)//findResource(searchPaths)

        //try it "normally" example with "/demo/foo.ftl"
        if (!resource)
            resource = findResource(uri);
        if (resource)
            log.debug("locate simple findResource success [$uri]")

        //try it based on what plugin the controller resides in
        if (!resource) {
            resource = locateWithController(uri);
        }
        //go nuclear and scan all the plugin paths
        if (!resource) {
            resource = findResourceInPlugins(uri);

        }
        return (ContextResource)resource;
    }

    /**
     * Attempts to resolve a uri in the search locations.
     *
     * @param uri The name
     * @return The Resouce of the template
     */
    ContextResource locateWithController(String uri) {

        GroovyObject controller = null;

        GrailsWebRequest webRequest = GrailsWebRequest.lookup();
        if (webRequest != null) {
            HttpServletRequest request = webRequest.getCurrentRequest();
            controller = webRequest.getAttributes().getController(request);
        }
        //just return if it can't find it
        if (!controller) return null

        return (ContextResource)locateWithController(controller, uri)
    }

    /**
     * Attempts to resolve a uri relative to a controller. Finds the right url if its in a plugin
     * Example: if the controller is Demo in the plugin CoolPlugin looks for a view index.xyz then
     * its going to try for "/plugins/CoolPlugin-1.1.2/grails-app/views/demo/index.xyz"
     *
     * @param controller The controller to resolve the template relative to
     * @param uri The view URI
     * @return The Resource of the view uri
     */
    ContextResource locateWithController(GroovyObject controller, String uri) {
        //just ditch out if controller is null
        if (!controller) return null

        Resource resource = null;

        //first check if its a controller in a plugin
        String pathToView = pluginManager != null ? pluginManager.getPluginViewsPathForInstance(controller) : null;
        if (pathToView != null) {
            String newURI = GrailsResourceUtils.appendPiecesForUri(pathToView, uri)
            resource = findResource(newURI)
            if (resource)
                log.debug("locateWithController newURI success [$newURI]")
        }

        return (ContextResource)resource;
    }

    /**
     * searches the list of paths
     *
     * @param searchPaths
     * @return a resource of ViewResource
     */
    @Override
    Resource findResource(List<String> searchPaths) {
        //ContextResource foundResource = null;
        Resource resource;
        log.debug("spinning through [${super.resourceLoaders.size()}] resourceLoaders")
        for (ResourceLoader loader : super.resourceLoaders) {
            log.debug("Using ResourceLoader [${loader.class}]")

            resource = findResource( loader, searchPaths)
            if (resource?.exists()) break;
        }
        return resource;
    }

    /**
     * searches the list of paths for a specific loader
     *
     * @param searchPaths
     * @return a resource of ViewResource
     */
    Resource findResource(ResourceLoader rloader, List<String> searchPaths) {
        Resource resource
        Resource foundResource
        log.debug("trying with ResourceLoader [$rloader]")

        for (String path : searchPaths) {
            log.debug("trying path [$path]")
            log.debug("trying with ResourceLoader [$rloader]")
            def r = rloader.getResource(path)

            if (r?.exists()) {
                log.debug("**** found resource [$r] with path [$path]")
                foundResource = new ViewContextResource(r.URI,path)
                break;
            }
        }

        return foundResource;
    }

    @Override
    protected Resource findResourceInPlugins(String uri) {
        Resource resource = super.findResourceInPlugins(uri)
        if (resource)
            log.debug("locate findResourceInPlugins success [$uri]")

        return resource
    }


    /**
     * creates a context path like you would get using the render method with a specified plugin
     *
     * @param uri the uri like "/xyz/index.ftl"
     * @param pluginName the name of the plugin such as "cool-plugin"
     * @return the path like "/plugins/cool-plugin-1.2.1/xyz/index.ftl"
     */
    public String getUriWithPluginPath(String uri, String pluginName){
        String pathToView = pluginManager.getGrailsPlugin(pluginName).getPluginPath()
        return GrailsResourceUtils.appendPiecesForUri(pathToView, uri)
    }

//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        super.addResourceLoader(applicationContext);
//
//        //GroovyPageResourceLoader bean exists in dev/test mode and deals with plugin paths, inplace etc... will not be here in deplyed war
//        if (applicationContext.containsBeanDefinition("groovyPageResourceLoader")) {
//            log.debug("must be runnning in dev so adding the groovyPageResourceLoader")
//            super.addResourceLoader((ResourceLoader)applicationContext.getBean("groovyPageResourceLoader"))
//        }
//    }

    protected String getNameForController(Object controller) {
        return GrailsNameUtils.getLogicalPropertyName(controller.getClass().getName(), ControllerArtefactHandler.TYPE);
    }


    /**
     * a simple resource that build from a URI and adds the base path the resource used to find it
     */
    public class ViewContextResource extends UrlResource implements ContextResource{
        private String pathWithinContext //the path given to a resourceLoader to find this Resource

        public ViewContextResource(URI uri, String pathWithinContext) {
            super(uri)
            this.pathWithinContext = pathWithinContext;
        }

        public String getPathWithinContext() {
            return pathWithinContext
        }
    }

    /**
     * FileSystemResource that explicitly expresses a context-relative path
     * through implementing the ContextResource interface.
     */
    private static class FileSystemContextResource extends FileSystemResource implements ContextResource {

        public FileSystemContextResource(String path) {
            super(path);
        }

        public String getPathWithinContext() {
            return getPath();
        }
    }
}
