nine {
	resources {

		currentTenant = { return [id: 1, num: "testTenant"] }
		views.location = "views"

		rootLocation = { args ->
			// AppResourcesService will not create rootLocation directory or an absolute path.  This is in target, so we need something extra.
			// For development we want a persistent non-SCM location common to all components but specific to a customer so we can
			// have customer data or common external files.
			File file = new File("test-projects/app/root-location")
			if (!file.exists()) {
				println "Creating rootLocation ${file.canonicalPath} for testing purposes."
				file.mkdirs()
			}
			return file.canonicalPath
		}
	}
}