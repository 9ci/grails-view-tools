/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package grails.plugin.viewtools

import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j

import org.apache.commons.lang.StringUtils
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.context.ApplicationContext
import org.springframework.util.Assert
import org.springframework.web.context.request.RequestContextHolder

import grails.core.GrailsApplication
import grails.plugin.viewtools.AppResourceLoader
import grails.plugins.GrailsPlugin
import grails.plugins.GrailsPluginManager
import grails.util.Environment

@CompileDynamic
@Slf4j
class AppSetupService {

    private static final String GRAILS_PLUGIN_SUFFIX = "GrailsPlugin"
    private static final String DEFAULT_CONFIG_SUFFIX = "AppSetupConfig"
    private static final String DEFAULT_CONFIG_FILE = "AppSetupConfig"

    AppResourceLoader appResourceLoader
    GrailsApplication grailsApplication

    /**
     * Add getSetupConfig method to grailsApplication
     */
    @SuppressWarnings(['SynchronizedOnThis', 'ExplicitHashMapInstantiation'])
    public void enhanceGrailsApplication() {
        MetaClass mc = grailsApplication.metaClass
        if (!mc.respondsTo(GrailsApplication, 'getSetupConfig')) {

            mc._cachedTenantConfigs = null
            mc._defaultAppSetupConfig = null
            //Holds config from *AppSetupConfig from all plguins (tenant specific configs arent merged in this)

            mc.getSetupConfig = { boolean reload = false ->

                synchronized (this) {
                    if (!delegate._cachedTenantConfigs) {
                        delegate._cachedTenantConfigs = new HashMap<String, ConfigObject>()
                    }

                    String tenant = appResourceLoader.tenantUniqueKey

                    if (reload) {
                        delegate._cachedTenantConfigs.remove(tenant)
                    }

                    if (!delegate._cachedTenantConfigs[tenant]) {
                        if (delegate._defaultAppSetupConfig == null || reload) {
                            delegate._defaultAppSetupConfig = loadDefaultConfig()
                        }

                        File setupLocation = appResourceLoader.getLocation('setup.location')
                        ConfigObject tenantConfig = null
                        if (setupLocation) {
                            tenantConfig = mergeTenantConfig(delegate._defaultAppSetupConfig, setupLocation.absolutePath)
                        } else {
                            tenantConfig = new ConfigObject()
                            tenantConfig.putAll(delegate._defaultAppSetupConfig)
                        }
                        processAppSetupConfig(tenantConfig)
                        delegate._cachedTenantConfigs[tenant] = tenantConfig
                    }

                    return delegate._cachedTenantConfigs[tenant]
                }
            }
        }
    }

    /**
     * Load all *AppSetupConfig.groovy from plugins (eg RallyAppSetupConfig.groovy)
     */
    @SuppressWarnings(['CatchException'])
    ConfigObject loadDefaultConfig() {
        ApplicationContext mainContext = grailsApplication.getMainContext()
        GrailsPluginManager pluginManager = (GrailsPluginManager) mainContext.getBean("pluginManager")

        def defaultConfigClasses = []

        for (GrailsPlugin plugin : pluginManager.getAllPlugins()) {
            Class<?> pluginClass = plugin.getPluginClass()

            String configName = pluginClass.getSimpleName()
            if (configName.endsWith(GRAILS_PLUGIN_SUFFIX)) {
                configName = configName.replace(GRAILS_PLUGIN_SUFFIX, DEFAULT_CONFIG_SUFFIX)
            } else {
                configName = configName + DEFAULT_CONFIG_SUFFIX
            }
            try {
                Class defaultConfigClass = grailsApplication.getClassLoader().loadClass(configName)
                defaultConfigClasses << defaultConfigClass
            } catch (ClassNotFoundException e) {
                log.trace("$configName Not found")
            }
        }

        //Load main application AppsetupConfig, eg RcmAppSetupConfig
        String appName = grailsApplication.config.getProperty("info.app.name", String)
        String appConfigName = StringUtils.capitalize(appName + DEFAULT_CONFIG_FILE)

        try {
            Class appConfigClass = grailsApplication.getClassLoader().loadClass(appConfigName)
            if (!defaultConfigClasses.contains(appConfigClass)) {
                defaultConfigClasses << appConfigClass
            }

        } catch (ClassNotFoundException e) {
            log.info("$appConfigName Not found")
        }

        //load nine.appsetup.config.files
        def configClasses = grailsApplication.config.nine.appsetup.config.files
        if (!configClasses || !(configClasses instanceof List)) configClasses = []
        if (configClasses) {
            log.info("Adding nine.appsetup.config.files - $configClasses")
            defaultConfigClasses.addAll(configClasses)
        }

        //make it possible for developers to create a developer specific rcm.appsetup config with name RcmDevSetupConfig.groovy
        if (Environment.current == Environment.DEVELOPMENT) {
            String devConfig = StringUtils.capitalize(appName + "DevSetupConfig")
            log.debug "Checking if developer config $devConfig exist"
            try {
                Class devConfigClass = grailsApplication.getClassLoader().loadClass(devConfig)
                if (!defaultConfigClasses.contains(devConfigClass)) {
                    defaultConfigClasses << devConfigClass
                }

            } catch (ClassNotFoundException e) {
                log.info("$devConfig Not found")
            }

        }

        ConfigObject defaultConfig = new ConfigObject()
        ConfigSlurper slurper = new ConfigSlurper(Environment.current.name)

        if (defaultConfigClasses) {
            defaultConfigClasses.each { Class configClass ->
                log.debug "Merging $configClass.simpleName"
                try {
                    ConfigObject config = slurper.parse(configClass)
                    defaultConfig.merge(config)
                } catch (Exception e) {
                    log.error("Error when merging config $configClass.simpleName", e)
                }
            }
        }

        log.info "setup.location: " + appResourceLoader.getLocation('setup.location')

        return defaultConfig
    }

    /**
     * Merge all *AppSetupConfig.groovy from given folder to defaultConfig
     */
    @SuppressWarnings(['CompileStatic', 'CatchException'])
    public ConfigObject mergeTenantConfig(ConfigObject defaultConfig, String setupLocation) {
        ConfigSlurper slurper = new ConfigSlurper(Environment.current.name)
        File dir = new File(setupLocation)
        Assert.isTrue(dir.exists())
        Assert.isTrue(dir.isDirectory())

        List files = dir.listFiles(new FilenameFilter() {
            @Override
            boolean accept(File d, String name) {
                return name.endsWith("AppSetupConfig.groovy")
            }
        })

        ConfigObject merged = new ConfigObject()
        merged.putAll(deepcopy(defaultConfig))

        files.each { File file ->
            log.debug "Merging $file.name"
            try {
                ConfigObject c = slurper.parse(file.text)
                merged.merge(c)
            } catch (Exception e) {
                log.error("Error during merging tenant config file $file.absolutePath", e)
            }
        }

        return merged
    }

    /**
     * Merges default actions with screen specific actions
     * @param config
     */
    private void processAppSetupConfig(ConfigObject config) {
        ConfigObject menuConfig = config.screens.defaultActions
        assert menuConfig != null
        assert !menuConfig.isEmpty()

        config.screens.remove("defaultActions")

        config.screens.each { screen, ConfigObject screenConfig ->
            ConfigObject merged = mergeConfig(menuConfig, screenConfig.menus)
            screenConfig.menus = merged

            /*
             * Merge list/show/row block level options with the default config to override defaults
             */
            merged.each { key, ConfigObject value ->
                ConfigObject list = value.remove('list')
                ConfigObject show = value.remove('show')
                ConfigObject row = value.remove('row')

                def mergedListConfig = mergeConfig(value, list)
                def mergedShowConfig = mergeConfig(value, show)
                def mergedRowConfig = mergeConfig(value, row)

                value.clear()

                list != null ? value.list = mergedListConfig : null
                show != null ? value.show = mergedShowConfig : null
                row != null ? value.row = mergedRowConfig : null
            }

        }

    }

    public ConfigObject mergeConfig(ConfigObject defaultConfig, override) {
        ConfigObject t = new ConfigObject()
        t.putAll(deepcopy(defaultConfig))
        if (override) {
            t.merge(override)
        }
        return t
    }

    /**
     * Deep copy given config object, to avoid merge issue
     *
     * Eg. Default menu config is merged with all screen level menus, by default the default menus config object is not deep copied while merging.
     * So a config key, which is in default config is modified at a screen level config, it affects all other screens too. Deep copying fixes that problem.
     */
    static deepcopy(ConfigObject orig) {
        ConfigObject copy = new ConfigObject()
        orig.keySet().each { key ->
            def value = orig.get(key)
            if (value instanceof ConfigObject) {
                value = deepcopy(value)
            }
            copy.put(key, value)
        }
        return copy
    }

    ConfigObject getScreenConfig(String screenName, boolean reload = false) {
        ConfigObject config = grailsApplication.getSetupConfig(reload).screens
        if (config?.containsKey(screenName)) {
            return config."$screenName"
        }
        else {
            return null
        }
    }

    @SuppressWarnings(['ReturnsNullInsteadOfEmptyCollection'])
    List<Map> getFormConfig(String screenName, String formName, boolean reload = false) {
        ConfigObject config = getScreenConfig(screenName, reload)?.forms
        if (config?.containsKey(formName)) {
            return config."$formName"
        }
        else {
            return null
        }
    }

    @SuppressWarnings(['ReturnsNullInsteadOfEmptyCollection'])
    List<Map> getPanelConfig(String screenName, String panelName, boolean reload = false) {
        ConfigObject config = getScreenConfig(screenName, reload)?.panels
        if (config?.containsKey(panelName)) {
            return config."$panelName"
        }
        else {
            return null
        }
    }

    public getValue(Object val) {
        if (val instanceof Closure) {
            Closure optionsClosure = grailsApplication.config.getProperty("appsetup.options.closure", Closure)
            GrailsWebRequest req = (GrailsWebRequest) RequestContextHolder.currentRequestAttributes()
            Map options = [
                    controller     : req.controllerName,
                    action         : req.actionName,
            ]

            if(optionsClosure) {
                //Add all additional options
                options.putAll(optionsClosure.call())
            }
            return val.call(options)

           /*
            def opts = [
                controller     : req.controllerName,
                action         : req.actionName,
                roles          : secService.principalRoles,
                isBranchLogin  : secService.ifAllGranted(SecRole.BRANCH),
                isCustomerLogin: secService.ifAllGranted(SecRole.CUSTOMER)
            ]*/

        }
        return val
    }

}
