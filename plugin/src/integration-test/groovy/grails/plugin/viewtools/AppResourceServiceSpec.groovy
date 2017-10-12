package grails.plugin.viewtools

import grails.test.mixin.integration.Integration
import org.apache.commons.io.FileUtils
import org.springframework.core.io.Resource
import spock.lang.Ignore
import spock.lang.IgnoreRest
import spock.lang.Shared
import spock.lang.Specification

@Integration
class AppResourceServiceSpec extends Specification {

	@Shared AppResourceService appResourceService

	void cleanupSpec() {
		FileUtils.deleteDirectory(appResourceService.getLocation("attachments.location"))
	}

	def testCreateAttachmentFile_empty_noCreation() {
		when:
		def result = appResourceService.createAttachmentFile(1L, null,'txt', null)

		then:
		result == null
	}

	def testCreateAttachmentFile_string() {
		when:
		def CONTENT = 'hello, world!'
		def result = appResourceService.createAttachmentFile(2L, 'xyz','txt', CONTENT)

		then:
		result

		when:
		def datePart = new Date().format('yyyy-MM')

		then:
		result.location.endsWith("${datePart}/xyz_2.txt")
		result.file
		//assert result.file.absolutePath == "xxx"
		'xyz_2.txt' == result.file.name
		result.location == appResourceService.getRelativePath('attachments.location', result.file)
		result.file.exists()
		CONTENT.size() == result.file.size()
	}

	def testCreateAttachmentFile_bytes() {
		when:
		def CONTENT = 'hello, world!'.bytes
		def result = appResourceService.createAttachmentFile(2L, null,'txt', CONTENT)

		then:
		result
		result.location
		result.location.size() > 3
		result.location.endsWith(".txt")
		result.file
		'2.txt' == result.file.name
		result.location == appResourceService.getRelativePath('attachments.location', result.file)
		result.file.exists()
		CONTENT.size() == result.file.size()
	}

	def testCreateAttachmentFile_empty_data() {
		when:
		def result = appResourceService.createAttachmentFile(2L, null,'txt', null)

		then:
		result == null
	}

	def testCreateAttachmentFile_file() {
		when:
		File origFile = new File('src/integration-test/resources/grails_logo.jpg')
		def data = FileUtils.readFileToByteArray(origFile)
		File tmpFile = appResourceService.createTempFile('grails_logo.jpg', data)
		tmpFile.deleteOnExit()
		def result = appResourceService.createAttachmentFile(2L, null,'jpg', tmpFile)

		then:
		result
		result.location
		result.location.size() > 3
		result.location.endsWith(".jpg")
		result.file
		tmpFile.name+"_2.jpg" == result.file.name
		result.location == appResourceService.getRelativePath('attachments.location', result.file)
		result.file.exists()

		cleanup:
		result.file.delete()
		tmpFile.delete()
	}

	def testCreateTempFile_empty() {
		when:
		def file = appResourceService.createTempFile("hello.txt", null)

		then:
		file
		file.name.startsWith('hello')
		file.name.endsWith('txt')
		0 == file.size()
		file.name == appResourceService.getRelativeTempPath(file)
	}

	def testCreateTempFile_string() {
		when:
		def file = appResourceService.createTempFile("hello.txt", 'hello, world!')

		then:
		file
		file.name.startsWith('hello')
		file.name.endsWith('txt')
		13 == file.size()
		file.name == appResourceService.getRelativeTempPath(file)
	}

	def testCreateTempFile_bytes() {
		when:
		def bytes = 'hello, world!'.getBytes()
		def file = appResourceService.createTempFile("hello.txt", bytes)

		then:
		file
		file.name.startsWith('hello')
		file.name.endsWith('txt')
		bytes.size() == file.size()
		file.name == appResourceService.getRelativeTempPath(file)
	}

	def testDeleteTempUploadedFiles() {
		when:
		def file1 = appResourceService.createTempFile('file1.txt', 'hello, world!')
		def file2 = appResourceService.createTempFile('file2.txt', 'goodbye cruel world.')

		then:
		file1.exists()
		file2.exists()

		when:
		appResourceService.deleteTempUploadedFiles(
			"[ {'tempFilename':'${file1.name}','originalFilename':'file1.txt','extension':'txt','filesQueued':'0'},"
			+ "{'tempFilename':'${file2.name}','originalFilename':'file2.txt','extension':'txt','filesQueued':'0'}]"
		)

		then:
		!file1.exists()
		!file2.exists()
	}

	def testGetRootLocation1() {
		when:
		def dir = appResourceService.rootLocation

		then:
		dir != null
		//File base = new File('target/resources/virgin-2/')
		// This next line will fail if you change nine.attachments.directory in Config.groovy OR the test data for id=2
		dir.path.endsWith(new File('/rootLocation').path)
		dir.exists()
		dir.isDirectory()
		dir.canWrite()
	}

	def testGetTempDir() {
		when:
		def dir = appResourceService.getTempDir()

		then:
		dir
		dir.absolutePath.endsWith(new File(System.getProperty("java.io.tmpdir")).path)
		dir.exists()
		dir.isDirectory()
		dir.canWrite()
	}

	def testGetRootLocation2() {
		when:
		File root = appResourceService.rootLocation
		println "root location is ${root.absolutePath}"

		then:
		root
		root.exists()
		root.isDirectory()
	}

	def testGetLocation_absolute_scripts() {
		when:
		List scripts = appResourceService.scripts

		then:
		scripts[0].exists()
		scripts[0].isDirectory()
	}

	def testGetLocation_absolute_tempDir() {
		when:
		File temp = appResourceService.getTempDir()

		then:
		temp.exists()
		temp.isDirectory()
	}


	def testGetLocation_relative_checkImages() {
		when:
		File checkImages = appResourceService.getLocation('checkImage.location')
		println "checkImageDir is ${checkImages.absolutePath}"

		then:
		checkImages.exists()
		checkImages.isDirectory()
	}

	def test_getAttachmentsMonthDirectory() {
		when:
		def dir = appResourceService.getMonthDirectory('attachments.location')

		then:
		dir != null

		when:
		// This next line will fail if you change nine.attachments.directory in Config.groovy OR the test data for id=2
		def datePart = new Date().format('yyyy-MM')

		then:
		dir.path.endsWith(new File("/attachments/${datePart}").path)
		dir.exists()
		dir.isDirectory()
		dir.canWrite()
	}

	def test_getRelativeTempPath() {
		when:
		def dir = appResourceService.getTempDir()

		then:
		'blah' == appResourceService.getRelativeTempPath(new File(dir, 'blah'))
	}

	@Ignore
	def testGetRelativePath() {
		when:
		def tmp = appResourceService.getTempDir()
		def file = new File(tmp, 'blahBlah')

		then:
		appResourceService.getRelativePath('tempDir', file) == 'blahBlah'
	}

	def testMergeClientValues_emptyMap() {
		when:
		def result = appResourceService.mergeClientValues()

		then:
		result.tenantId==1
		result.tenantSubDomain=='testTenant'
		result.size() == 2
	}

	def testMergeClientValues_fullMap_noUnexpectedCollisions() {
		when:
		Map args = [tenantId:7, name:'blah', num: 'seventy', numbers:['one', 'two', 'three']]
		def result = appResourceService.mergeClientValues(args)

		then:
		result.tenantId == 7

		when:
		result.tenantId = 2 // Should not transfer back to args

		then:
		args.tenantId == 7
		result.tenantSubDomain == 'testTenant'
		args.size() == 4
		result.size() == 5
		result.numbers[1] == 'two'
		args.numbers[1] == 'two'

		when:
		result.numbers[1] = 'blah' // Replaces a deep value.

		then:
		args.numbers[1] == 'blah' // Bad, but expected.
		args.num == 'seventy'

		when:
		result.num = 'seven' // Should be a shallow replacement, not passed back to args.

		then:
		args.num == 'seventy' // Should be a the same as before.
	}


	def "test get resource"() {
		setup:
		File origFile = new File('src/integration-test/resources/grails_logo.jpg')
		def data = FileUtils.readFileToByteArray(origFile)

		File viewsDirectory = appResourceService.getLocation("views.location")

		assert viewsDirectory.exists()

		File viewFile = new File(viewsDirectory, "test.view")
		FileUtils.writeByteArrayToFile(viewFile, data)

		expect:
		viewFile.exists()

		when:
		Resource resource = appResourceService.getResource("views/test.view")

		then:
		resource.exists()

		when:
		resource = appResourceService.getResourceRelative("config:views.location", "test.view")

		then:
		resource.exists()

		cleanup:
		viewFile.delete()
	}
}
