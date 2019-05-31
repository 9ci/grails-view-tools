AppSetupConfig is different then grails config. Appsetup config is accessible by `grailsApplication.setupConfig`.

### How and from where appsetup config files loaded

Appsetupconfig is loaded and merged by AppSetupService

1. First, it checks all installed plugins, to see if plugin provides one appsetup config file. the plugins appsetup config file name must be PluginNameAppSetupConfig (eg RallyAppSetupConfig or AutocashAppSetupConfig). all of the plugin appsetup config file will be merged together.

2. Second it will check if AppNameAppSetupConfig file exist eg (RcmAppSetupConfig), if exist, it will be used and merged with the config created in step 1. So, Applications AppSetupConfig can override any values in plugins Appsetup config.

3. For development environment **only**, it will check if AppNameDevSetupConfig (eg RcmDevSetupConfig) exists, if yes, it will be merged with the config created in step2. so RcmDevSetupConfig can override any values in files in previous steps.

4. Next if, **app.resources.setup.location** is configured, it will find all files with name ending with AppSetupConfig.groovy (eg ClientXAppSetupConfig.groovy) from the directory specified in app.resources.setup.location. all these files will be merged with the config created in previos steps. so files in setup.location can override values in files from all previos steps.


By default, once the Appsetupconfig is parsed, it will be cached, making any changes to any of the files mentioned above will have no effect. however appsetup config can be reloaded by calling grailsApplication.getSetupConfig(true). by passing true, application can be asked to reload appsetup config.

**Configuration**

Plugin needs currentTenantClosure and rootLocation configured.

Example

*application.groovy*
```groovy

app {
    resources {
        currentTenant = { return [id: 1, num: "test-tenant"] }
        setup.location = "setup" //directory under rootlocation where tenant specific config files can be put

        rootLocation = { args ->
            File file = new File("PATH TO ROOT LOCATION")
            if (!file.exists()) {
                println "Creating rootLocation ${file.canonicalPath}"
                file.mkdirs()
            }
            return file.canonicalPath
        }

    }
}

```
