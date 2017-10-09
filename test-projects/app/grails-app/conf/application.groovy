nine {
	resources {

		currentTenant = { return [id: 1, num: "testTenant"] }
		views.location = "views"

		rootLocation = { args ->
			File file = new File("test-projects/app/root-location")
			if (!file.exists()) {
				println "Creating rootLocation ${file.canonicalPath} for testing purposes."
				file.mkdirs()
			}
			return file.canonicalPath
		}
	}
}