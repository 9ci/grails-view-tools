
nine {
	resources {

		currentTenant = { return [id:1, num:"testTenant"]}
		tempDir = {
			def props = System.properties
			File tmp = new File("${props['java.io.tmpdir']}/${props['user.name']}/9ci-app/temp")
			if(!tmp.exists()) tmp.mkdirs() // appResourceService doesn't want to create directories which are not inside rootLocation.
			return tmp.canonicalPath // In order for appResourceService to allow it to be outside of rootLocation it must be absolute.
		}
		attachments.location = 'attachments'

		rootLocation = { args ->
			// AppResourcesService will not create rootLocation directory or an absolute path.  This is in target, so we need something extra.
			// For development we want a persistent non-SCM location common to all components but specific to a customer so we can
			// have customer data or common external files.
			File file = new File("build/rootLocation")
			if(!file.exists()) {
				println "Creating rootLocation ${file.canonicalPath} for testing purposes."
				file.mkdirs()
			}
			return file.canonicalPath
		}

		scripts{
			// For test we want two script locations:
			// 1. The primary (first) location needs to be what's checked into SCM.
			//    * We have tests which examine the first scripts location for files.
			// 2. The default target/rootLocation/scripts.
			//    * We have tests which create a script and execute it.
			locations = { args ->
				// AppResourcesService will not create an absolute directory.  This is not in rootLocation, so we need something extra.
				File file = new File('src/integration-test/resources')
				if(!file.exists()) {
					println "Creating ${file.canonicalPath} for testing purposes."
					file.mkdirs()
				}
				return [ file.canonicalPath, 'scripts' ]
			}
		}
	}
}