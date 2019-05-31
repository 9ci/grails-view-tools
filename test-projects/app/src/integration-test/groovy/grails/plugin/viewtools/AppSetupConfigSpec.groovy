package grails.plugin.viewtools

import grails.core.GrailsApplication
import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files

@Integration
@Rollback
class AppSetupConfigSpec extends Specification {

    GrailsApplication grailsApplication
    @Shared String setupLocationStr = "root-location/setup"
    ConfigObject config

    void setup() {
        if(!config) {
            config = grailsApplication.getSetupConfig(true)
        }
    }

    def setupSpec() {
        File setupLocation = new File(setupLocationStr)
        if(!setupLocation.exists()) {
            setupLocation.mkdirs()
        }

        File source = new File("src/integration-test/resources/TestTenantAppSetupConfig.groovy")
        File dest = new File(setupLocation, "TestTenantAppSetupConfig.groovy")
        Files.copy(source.toPath(), dest.toPath())
        assert dest.exists()
    }

    def cleanupSpec() {
        File setupLocation = new File(setupLocationStr)
        File dest = new File(setupLocation, "TestTenantAppSetupConfig.groovy")
        if(dest.exists()) {
            dest.delete()
        }
    }

    void "test default actions are merged"() {
        expect:
        config.screens.test.menus.open != null
        config.screens.test.menus.open.show.label == "Open"
    }

    void "test appsetup config from the main app is loaded"() {
        expect:
        config.screens.test.delete != null
        config.screens.test.delete.label == "Delete"
    }

    void "test appsetup specified in nine.appsetup.config.files is loaded"() {
        expect:
        config.screens.test.delete != null
        config.screens.test.delete.icon == "fa-delete"
    }

    void "test appsetup config from  tenant directory is loaded"() {
        expect:
        config.screens.test.delete != null
        config.screens.test.delete.ngClick == "delete()"
    }

}
