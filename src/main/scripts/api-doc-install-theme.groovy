description "Install API Doc Boostrap Theme", """
	Usage: grails api-doc-install-theme <theme-name>

	Installs a Bootstrap theme for ApiDocs from https://bootswatch.com/
	Available Themes:
		cerulean
		cosmo
		cyborg
		darkly
		flatly
		journal
		lumen
		paper
		readable
		sandstone
		simplex
		slate
		spacelab
		superhero
		united
		yeti

	Example: grails api-doc-install-theme darkly
"""

templates("META-INF/assets/apidoc/themes/"+args[0]+"/*").each { r ->
    String path = r.URL.toString().replaceAll(java.util.regex.Pattern.compile("^.*?META-INF/assets/apidoc/themes/"+args[0]), "grails-app/assets/resources/apidoc/style")
    println path
    if (path.endsWith('/')) {
        mkdir(path)
    } else {
    	File parent = new File("grails-app/assets/resources/apidoc/style")
    	parent.mkdirs()
        File to = new File(path)
        org.grails.io.support.SpringIOUtils.copy(r, to)
        println("Copied ${args[0]} theme to ${path}")
    }
}
