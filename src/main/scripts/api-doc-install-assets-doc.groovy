description "Install assets based API Doc file", """
	Usage: grails api-doc-install-assets-doc 

	Copies static js file to grails-app/assets/resources/apidoc/data/apiDocObj.js

	Example: grails grails api-doc-install-asset-doc
"""

templates("META-INF/assets/apidoc/data/apiDocObj.js").each { r ->
    String path = r.URL.toString().replaceAll(/^.*?META-INF\/assets/, "grails-app/assets/resources")
    if (path.endsWith('/')) {
        mkdir(path)
    } else {
        File parent = new File("grails-app/assets/resources/apidoc/data")
        parent.mkdirs()
        File to = new File(path)
        org.grails.io.support.SpringIOUtils.copy(r, to)
        println("Copied ${path}")
        println("Customimze API docs by editing grails-app/assets/resources/apidoc/data/apiDocObj.js")
    }
}