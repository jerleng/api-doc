description "Install API Doc Assets", """
	Usage: grails api-doc-install-assets 

	Copies all API assets from the plugin to the project

	Example: grails grails api-doc-install-assets 
"""

templates("META-INF/assets/apidoc/**/*").each { r ->
    String path = r.URL.toString().replaceAll(/^.*?META-INF\/assets/, "grails-app/assets/resources")
    if (path.endsWith('/')) {
        mkdir(path)
    } else {
        File to = new File(path)
        org.grails.io.support.SpringIOUtils.copy(r, to)
        println("Copied ${path}")
    }
}