package apidoc

import grails.converters.*

import org.springframework.transaction.*
import static org.springframework.http.MediaType.*

import apidoc.*

class ApiDocController {
	static namespace = 'apiDoc'
	static String JSON_PAYLOAD
	static String MANIFEST

	def groovyPageRenderer
	
	private imageResources(images, item) {
		item?.docResources?.each {dr->
			if (dr.type == 'DESCRIPTION_IMAGE' || dr.type == 'SCREENSHOT') {
				images << dr.uri
			}
		}
	}

	private synchronized void refresh() {
		log.debug '>>>> refeshing API Doc JSON object and MANIFEST doc'
		def result = ApiDocBuilder.build()
		def refs = [],
			images = []
		imageResources(images, result)
		result.objects?.each {
			refs << it.ref
			imageResources(images, it)
		}
		result.flows?.each {
			refs << it.ref
			imageResources(images, it)
		}
		result.apis?.each {
			refs << it.ref
			imageResources(images, it)
			it.methods?.each {m->
				refs << m.ref
				imageResources(images, m)
			}
		}
		result.basePath = result.basePath ?: g.createLinkTo(dir:'', absolute:true).replaceAll('/static','')
		MANIFEST = groovyPageRenderer.render(view:'/apiDoc/manifest', model:[refs:refs, images:images])
		JSON_PAYLOAD = new JSON(result).toString(true)
	}

	def data() {
		if (grails.util.Holders.grailsApplication.config.apidoc.useAssetsDoc == true) {
			log.info('API Docs using /apidoc/data/apiDocObj.js')
			redirect uri:g.assetPath(src: '/apidoc/data/apiDocObj.js'), contentType:'text/javascript'
			return
		}

		if (!JSON_PAYLOAD || params.boolean('refresh')) {
			refresh()
		}
		if (params.jsonp) {
			def jsonP = params.jsonp+'('+JSON_PAYLOAD+');'
			render text:jsonP, contentType:'text/javascript'
		} else if (params.jsonv) {
			def jsonV = 'var '+params.jsonv+' = '+JSON_PAYLOAD+';'
			render text:jsonV, contentType:'text/javascript'
		} else {
			render text:JSON_PAYLOAD, contentType:'application/json'
		}
	}
	// don't really need this except to update timestamp and dynamically include doc resource images
	def manifest() {
		if (!MANIFEST || params.boolean('refresh')) {
			refresh()
		}

		render text:MANIFEST, contentType:'text/cache-manifest'
	}

	// BEGIN TEST STUFF

	def testList() {
		setApiResponseFormat()
		if(!params.id) {
			render text: 'Objec type parameter is required', status: 400
			return
		}

		Map rtn = [
			success:false,
			data: [:],
			feedback: [:],
			meta: [:]
		]
		rtn.data[params.id+'s'] = [
			[name:'poop', description: 'ew'],
			[name:'pee', description: 'ick'],
			[name:'doodoo', description: 'nope']
		]
		rtn.meta.total = rtn.data.loanSources?.size() ?: 0
		rtn.meta.count = rtn.meta.total
		rtn.feedback.messages = ['default':'active loan sources retrieved']
		rtn.success = true
		switch(request.apiFormat) {
			case 'json': 
				def payloadStr = new JSON(rtn).toString(params.prettyPrint?.toString() == 'true') 
				request["Response-Content-Length"] = payloadStr.length()
				render text:payloadStr, contentType:'application/json'
				break
			case 'xml':
				def payloadStr = new XML(rtn).toString() 
				request["Response-Content-Length"] = payloadStr.length()
				render text:payloadStr, contentType:'text/xml'
				break
			case 'text':
				def payloadStr = rtn.data[params.id+'s'].collect{it.name+':'+it.description}.join('\n')
				request["Response-Content-Length"] = payloadStr.length()
				render text:payloadStr, contentType:'text/plain'
				break
			default:
				def writer = new StringWriter()
				def markup = new groovy.xml.MarkupBuilder(writer)
				markup.html { 
					head {
						title params.id+' List'
					}
					body {
						h1 params.id+' List'
						table(style:'border:1px solid black', cellspacing:'0', cellpadding:'10') {
							tr {
								th(style:'border:1px solid black', 'Name')
								th(style:'border:1px solid black', 'Description')
							}
							rtn.data[params.id+'s'].each {ls->
								tr {
									td(style:'border:1px solid black', ls.name)
									td(style:'border:1px solid black', ls.description)
								}
							}
						}
					}
				}
				String payloadStr = writer.toString()
				request["Response-Content-Length"] = payloadStr.length()
				render text:payloadStr, contentType:'text/html'
				break			
		}
	}

	def testSave() {
		setApiResponseFormat()
		if(!params.id) {
			render text: 'Objec type parameter is required', status: 400
			return
		}
		Map rtn = [
			success:true,
			data: [:],
			feedback: [messages:['default':params.id +' has been saved']],
			meta: [:]
		]
		switch(request.apiFormat) {
			case 'json': 
				def payloadStr = new JSON(rtn).toString(params.prettyPrint?.toString() == 'true') 
				request["Response-Content-Length"] = payloadStr.length()
				render text:payloadStr, contentType:'application/json'
				break
			case 'xml':
				def payloadStr = new XML(rtn).toString() 
				request["Response-Content-Length"] = payloadStr.length()
				render text:payloadStr, contentType:'text/xml'
				break
			case 'text':
				def payloadStr = params.id+' Saved'
				request["Response-Content-Length"] = payloadStr.length()
				render text:payloadStr, contentType:'text/plain'
				break
			default:
				def writer = new StringWriter()
				def markup = new groovy.xml.MarkupBuilder(writer)
				markup.html { 
					head {
						title params.id+' Saved'
					}
					body {
						h1 params.id+' Saved'
					}
				}
				String payloadStr = writer.toString()
				request["Response-Content-Length"] = payloadStr.length()
				render text:payloadStr, contentType:'text/html'
				break			
		}
	}	
	private setApiResponseFormat() {
		def h = request.getHeader('Accept')
		if (!params.format && (!request.format || request.format == 'all') && h) {
			def mimeTypes = grailsApplication.config.grails.mime.types,
				match
			grailsApplication.config.grails.mime.types.each {
				match = match ?: (h.contains(it.value.toString()) ? it.key : null)
				if (!match && it.value instanceof List) {
					it.value.each {it2->
						match = match ?: (h.contains(it2.toString()) ? it.key : null)
					}
				}
			}
			request.apiFormat = match
		} else {
			request.apiFormat = request.format != 'all' ? request.format : params.format
		}
	}	
	// END TEST STUFF

}