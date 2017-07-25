package apidoc

class ApiDocUrlMappings {

    static mappings = {
        "/api/doc/data/jsonv"(controller:"apiDoc", action:"data") {
            jsonv = 'apiDocObj'
        }
        "/api/doc/data"(controller:"apiDoc", action:"data")
        "/api/doc/manifest"(controller:"apiDoc", action:"manifest")
    }
}
