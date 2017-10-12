
nine {
	resources {

		currentTenant = { return [id:1, num:"testTenant"]}

		attachments.location = 'attachments'
		views.location = "views"
		checkImage.location = "checkImages"

		rootLocation = { args ->
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