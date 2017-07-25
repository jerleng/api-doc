package apidoc

import groovy.util.logging.Log
import apidoc.annotation.*
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.springframework.context.i18n.LocaleContextHolder
import static grails.util.GrailsNameUtils.*
import java.lang.reflect.*
import static apidoc.annotation.ApiDocConstants.*

class ApiDocBuilder {
	static Boolean USE_ASSETS_API_DOC = false
	static String DEFAULT_TYPE
	static String CONTROLLER_PREFIX
	static String CONTROLLER_SUFFIX
	static String DEFAULT_FORMAT
	static String DEFAULT_FORMAT_NAME
	static String GRAILS_DOMAIN_DEFAULT_TYPE
	static String DEFAULT_PARAMS_QUERY_ALL
	static String DEFAULT_PARAMS_QUERY_SINGLE
	static String DEFAULT_PARAMS_QUERY_MULTIPLE
	static String DOMAIN_OBJECT_FIELDS
	static String VERSION
	static String POJO_PKG_TO_SCAN
	static String INTRO_MESSAGE
	static String DEFAULT_UPDATES_MESSAGE
	static String DEFAULT_CATEGORY
	static String PATH_PREFIX
	static String APP_NAME
	static INTRO_DOC_RESOURCES
	static String BASEPATH
	static Boolean PLAYGROUND_ENABLED
    static Boolean ADD_API_KEY
    static Boolean ADD_TOKEN
    static Boolean ADD_API_KEY_IS_HEADER
    static String API_KEY_PARAM_NAME = 'apiKey'
    static String API_DOC_TITLE
    static String API_TITLE
    static String OBJECTS_TITLE
    static String FLOWS_TITLE
	static EXCLUDED = ['metaClass','class','beforeDelete', 'delegate','instanceControllersDomainBindingApi'
	   ,'beforeUpdate','beforeInsert','afterUpdate','transients', 'grailsApplication','instanceDatabindingApi'
	   ,'afterInsert','afterDelete','hibernateLazyInitializer','mapping','constraints','instanceGormValidationApi'
	   ,'compareTo', 'dataSource','className','maxState','errors','instanceGormInstanceApi','log','instanceConvertersApi'
	   ,'$staticClassInfo', '$changedProperties','__$stMC','metaClass','__timeStamp','$callSiteArray'
	   ,'restExcluded','staticGormStaticApi'
	   ]

	static readConfig(grailsApplication) {
		INTRO_DOC_RESOURCES = grailsApplication.config.apidoc.containsKey('introDocResources') ? grailsApplication.config.apidoc.introDocResources : null
		PATH_PREFIX = grailsApplication.config.apidoc.containsKey('pathPrefix') ? grailsApplication.config.apidoc.pathPrefix +'/' : ''
		DEFAULT_TYPE = grailsApplication.config.apidoc.defaultResponseType
		DEFAULT_CATEGORY = grailsApplication.config.apidoc.defaultCategory
		DEFAULT_FORMAT = grailsApplication.config.apidoc.defaultFormat
		DEFAULT_FORMAT_NAME = grailsApplication.config.apidoc.defaultFormatString
		GRAILS_DOMAIN_DEFAULT_TYPE = grailsApplication.config.apidoc.grailsDomainDefaultType
		DOMAIN_OBJECT_FIELDS = grailsApplication.config.apidoc.defaultObjectFields
		DEFAULT_PARAMS_QUERY_ALL = grailsApplication.config.apidoc.defaultParamsQueryAll
		DEFAULT_PARAMS_QUERY_SINGLE = grailsApplication.config.apidoc.defaultParamsQuerySingle
		DEFAULT_PARAMS_QUERY_MULTIPLE = grailsApplication.config.apidoc.defaultParamsQueryMultiple
		VERSION = grailsApplication.metadata['info.app.version']//grailsApplication.config.grails.plugins.apidoc.docVersion
		POJO_PKG_TO_SCAN = grailsApplication.config.apidoc.packageToScan
		BASEPATH = grailsApplication.config.grails.containsKey('serverUrl') ? grailsApplication.config.grails.serverUrl : null
		BASEPATH = BASEPATH ?: (grailsApplication.config.grails.containsKey('serverURL') ? grailsApplication.config.grails.serverURL : null)
		PLAYGROUND_ENABLED = grailsApplication.config.apidoc.playgroundEnabled != false
        ADD_API_KEY = grailsApplication.config.apidoc.addApiKey == true
        ADD_TOKEN = grailsApplication.config.apidoc.addToken == true
        ADD_API_KEY_IS_HEADER = grailsApplication.config.apidoc.apiKeyAsHeader == true
        API_KEY_PARAM_NAME = grailsApplication.config.apidoc.apiKeyParamName ?: 'apiKey'
		APP_NAME = grails.util.GrailsNameUtils.getNaturalName(grailsApplication.metadata['info.app.name'] ?: 'agoraData')
		def messageSource = grailsApplication.mainContext['messageSource']
		INTRO_MESSAGE = messageSource.getMessage('apidoc.introMessage', [APP_NAME].toArray(), LocaleContextHolder.locale)
		DEFAULT_UPDATES_MESSAGE = messageSource.getMessage('apidoc.defaultUpdatesMessage', null, LocaleContextHolder.locale)
        API_DOC_TITLE = grailsApplication.config.apidoc.title ?: 'API Info'
        API_TITLE = grailsApplication.config.apidoc.apisTitle ?: 'APIs'
        OBJECTS_TITLE = grailsApplication.config.apidoc.objectsTitle ?: 'Objects'
        FLOWS_TITLE = grailsApplication.config.apidoc.flowsTitle ?: 'Flows'
        if (grailsApplication.config.apidoc.useAssetsDoc) {
        	USE_ASSETS_API_DOC = grailsApplication.config.apidoc.useAssetsDoc == true
        }
	}	
	@SuppressWarnings("rawtypes")
	static uuid() {
		org.apache.commons.lang.RandomStringUtils.random(6,true,false)
		//UUID.randomUUID().toString()
	}

	static hints(a) {
		def hints = a.hints() ?: []
		def messageSource = grails.util.Holders.grailsApplication.mainContext['messageSource']
		a.hintCodes()?.each {
			try {
				hints << messageSource.getMessage(it, null, LocaleContextHolder.locale)
			} catch (Throwable e) {
				hints << it
			}
		}
		return (hints.size() > 0 ? hints : hints)
	}

	static warnings(a) {
		def warnings = a.warnings() ?: []
		def messageSource = grails.util.Holders.grailsApplication.mainContext['messageSource']
		a.warningCodes()?.each {
			try {
				hints << messageSource.getMessage(it, null, LocaleContextHolder.locale)
			} catch (Throwable e) {
				hints << it
			}
		}
		return (warnings.size() > 0 ? warnings : warnings)
	}

	@SuppressWarnings("rawtypes")
	static buildApiMethodDocFromAnnotatedMember(apiDoc, member) {
		def a = member.getAnnotation(ApiMethod.class)
		def methodDoc = [
			ref:('method-'+apiDoc.ref+'-'+(a.name() ?: member.name)).replaceAll(' ','-'),
			id:a.id(),
			verbs:a.verbs(),
			path:a.path(),
			description:a.description() ?: null,
			warnings:warnings(a),
			hints:hints(a),
			name:a.name() ?: member.name,
			headers:null,
			consumes:a.consumes() ?: null,
			produces:a.produces() ?: null,
			responseCode:a.responseCode(),
			snippet: a.snippet() ?: null,
			extraQueryString: a.extraQueryString(),
			enablePlayground: a.enablePlayground() != false,
			request:null,
			response:null,
			pathparameters:null,
			queryparameters:null,
			apiErrors:null,
			sinceVersion:a.sinceVersion() ?: null,
			untilVersion:a.untilVersion() ?: null
		]
		processDocResources(methodDoc, a)
		if (member.isAnnotationPresent(ApiHeaders)) {
			a = member.getAnnotation(ApiHeaders.class)
			a.value()?.each {paramA->
				def prmDoc = [
					ref:uuid(),
					warnings:warnings(paramA),
					hints:hints(paramA),
					name:paramA.name(),
					description:paramA.description() ?: null,
					required:paramA.required(),
					defaultValue:paramA.defaultValue() ?: null,
					format:paramA.format() ?: null,
					allowedValues:paramA.allowedValues(),
					sinceVersion:paramA.sinceVersion() ?: null,
					untilVersion:paramA.untilVersion() ?: null
				]
				methodDoc.headers = methodDoc.headers ?: []
				methodDoc.headers << prmDoc
			}
		}		
        def apiKeyFound = false,
        	tokenFound = false,
        	noApiKey = a.noApiKey(),
        	noToken = a.noToken()
		if (member.isAnnotationPresent(ApiParams)) {
			a = member.getAnnotation(ApiParams.class)
			a.value()?.each {paramA->
                apiKeyFound = apiKeyFound || paramA.name() == API_KEY_PARAM_NAME
                tokenFound = tokenFound || paramA.name() == 'token'
				def prmDoc = [
					ref:uuid(),
					allowMultiple: paramA.allowMultiple(),
					typeInfo: [isList:false, label:paramA.type(), type:paramA.type()],
					warnings:warnings(paramA),
					hints:hints(paramA),
					name:paramA.name(),
					show:paramA.show(),
					required:paramA.required()
				]
				if (paramA.format()) prmDoc.format = paramA.format()
				if (paramA.defaultValue()) prmDoc.defaultValue = paramA.defaultValue()
				if (paramA.description()) prmDoc.description = paramA.description()
				if (paramA.allowedValues()) prmDoc.allowedValues = paramA.allowedValues()
				if (paramA.sinceVersion()) prmDoc.sinceVersion = paramA.sinceVersion()
				if (paramA.untilVersion()) prmDoc.untilVersion = paramA.untilVersion()

				if (paramA.paramType() == QUERY) {
					methodDoc.queryparameters = methodDoc.queryparameters ?: []
					methodDoc.queryparameters << prmDoc
				}
				if (paramA.paramType() == PATH) {
					methodDoc.pathparameters = methodDoc.pathparameters ?: []
					methodDoc.pathparameters << prmDoc
				}
			}
		}
		if (noToken == false && tokenFound == false && ADD_TOKEN) {
			def prmDoc = [
				ref:uuid(),
				allowMultiple: false,
				typeInfo: [isList:false, label:'string', type:'string'],
				name:'token',
				show:true,
				defaultValue:'',
				description:'API token that may be required if configured or if an attempt receives a 401 with WWW-Authization header "auth-vault-token"',
				required:false
			]
			methodDoc.queryparameters = methodDoc.queryparameters ?: []
			methodDoc.queryparameters.add(0,prmDoc)
//			println 'added to '+methodDoc.name 
		}

		def apiKeyAnnotationFound = member.getDeclaredAnnotations().find { it.annotationType().simpleName.toLowerCase() == 'apikeyrequired'} != null
		if (noApiKey == false && apiKeyFound == false && (apiKeyAnnotationFound || ADD_API_KEY)) {
			def prmDoc = [
				ref:uuid(),
				allowMultiple: false,
				typeInfo: [isList:false, label:'string', type:'string'],
				name:API_KEY_PARAM_NAME,
				show:true,
				defaultValue:'',
				description:'API Key associated with account',
				required:true
			]
			if (ADD_API_KEY_IS_HEADER) {
				methodDoc.headers = methodDoc.headers ?: []
				methodDoc.headers.add(0,prmDoc)
			} else {
				methodDoc.queryparameters = methodDoc.queryparameters ?: []
				methodDoc.queryparameters.add(0,prmDoc)

			}
//			println 'added to '+methodDoc.name 
		}
		if (member.isAnnotationPresent(ApiErrors)) {
			a = member.getAnnotation(ApiErrors.class)
			a.value()?.each {paramA->
				def prmDoc = [
					ref:uuid(),
					warnings:warnings(paramA),
					hints:hints(paramA),
					code:paramA.code() ?: null,
					description:paramA.description() ?: null,
					sinceVersion:paramA.sinceVersion() ?: null,
					untilVersion:paramA.untilVersion() ?: null
				]
				methodDoc.apiErrors = methodDoc.apiErrors ?: []
				methodDoc.apiErrors << prmDoc
			}
		}
		if (member.isAnnotationPresent(ApiBodyObject)) {
			a = member.getAnnotation(ApiBodyObject.class)
			def prep = ''
			if (a.isList()) prep = 'List of '			
			else if (a.isMap()) prep = 'Map of '			
			def label = prep + a.objectType()
			if (a.wrapperObjectType()) label = a.wrapperObjectType()+' with '+label

			methodDoc.request = [
				ref:uuid(),
				typeInfo: [isList:a.isList(), isMap:a.isMap(), label:label, subObjectTypes: a.subObjectTypes(), wrapperObjectType: a.wrapperObjectType(), type:a.objectType()],
				warnings:warnings(a),
				hints:hints(a)
			]
		}		
		if (member.isAnnotationPresent(ApiResponseObject)) {
			a = member.getAnnotation(ApiResponseObject.class)
			def prep = ''
			if (a.isList()) prep = 'List of '			
			else if (a.isMap()) prep = 'Map of '			
			def label = prep + a.objectType()
			if (a.wrapperObjectType()) label = a.wrapperObjectType()+' with '+label
			methodDoc.response = [
				ref:uuid(),
				typeInfo: [isList:a.isList(), isMap:a.isMap(), label:label, subObjectTypes: a.subObjectTypes(), wrapperObjectType: a.wrapperObjectType(), type:a.objectType()],
				warnings:warnings(a),
				hints:hints(a)
			]
		}
		if (member.isAnnotationPresent(ApiAuthBasic)) {
			methodDoc.auth = [
				type: 'Basic Auth'
			]
		}
		if (ADD_API_KEY) {
			methodDoc.auth = methodDoc.auth ?: [type:'']
			methodDoc.auth.type = methodDoc.auth.type ? methodDoc.auth.type+' + ':''
			methodDoc.auth.type += (ADD_API_KEY?'API Key'+(ADD_API_KEY_IS_HEADER ? ' (HTTP header)':' (query string parameter)'):'')
		} 
		methodDoc 
	}

	@SuppressWarnings("rawtypes")
	static buildApiDocFromAnnotatedMember(member) {
		def a = member.getAnnotation(Api.class)
		def apiDoc = [
			ref:('api-'+(a.name() ?: uuid())).replaceAll(' ','-'),
			name: a.name(),
			category:a.category() ?: '',
			description:a.description() ?: null,
			warnings:warnings(a),
			hints:hints(a),
			snippet: a.snippet() ?: null,
			methods:[],
			sinceVersion:a.sinceVersion() ?: null,
			untilVersion:a.untilVersion() ?: null
		]
		processDocResources(apiDoc, a)
		apiDoc
	}

	@SuppressWarnings("rawtypes")
	static buildApiObjectFieldDocFromAnnotation(a, defaultName = null) {
		def prepend = ''
		if (a.isList()) prepend = 'List of '
		else if (a.isMap()) prepend = 'Map of '
		def label = prepend + a.allowedType()
		def fieldDoc = [
			ref:uuid(),
			typeInfo: [isList:a.isList(), isMap:a.isMap(), label:label, type:a.allowedType()],
			name: a.name() ?: defaultName,
			required:a.required(),
			readOnly:a.readOnly(),
			warnings:warnings(a),
			hints:hints(a)
		]
		if (a.format()) fieldDoc.format = a.format()
		if (a.description()) fieldDoc.description = a.description()
		if (a.defaultValue()) fieldDoc.defaultValue = a.defaultValue()
		if (a.allowedValues()) fieldDoc.allowedValues = a.allowedValues()
		if (a.sinceVersion()) fieldDoc.sinceVersion = a.sinceVersion()
		if (a.untilVersion()) fieldDoc.untilVersion = a.untilVersion()

		fieldDoc
	}

	@SuppressWarnings("rawtypes")
	static buildApiObjectFieldDocFromField(a) {
		def fieldDoc = [
			ref:uuid(),
			typeInfo: [isList:null, label:a.type?.simpleName, type:a.type?.simpleName],
			name: a.name
		]
		fieldDoc
	}

	static processDocResources(doc, a) {
		def grailsApplication = grails.util.Holders.grailsApplication
		def generator = grailsApplication.mainContext['grailsLinkGenerator']	
		a.docResources().each {r->
			def uri = r.uri()
			if (!uri) {
				uri = generator.link(controller:r.controller() ?: null, action:r.action() ?: null)
			} else if (r.resourceType().endsWith('ASSET')) {
				uri = generator.resource(src:uri)
			}

			doc.docResources = doc.docResources ?: []
			doc.docResources << [type:r.resourceType().replaceAll('_ASSET', ''), title:r.title(),uri:uri, description:r.description()]
		}	
	}

	@SuppressWarnings("rawtypes")
	static buildApiObjectDocFromAnnotatedMember(member) {
		def a = member.getAnnotation(ApiObject.class)
		def objectDoc = [
			ref:('object-'+(a.name() ?: (member instanceof Class ? member.simpleName : member.name).capitalize())).replaceAll(' ','-'),
			category:a.category() ?: '',
			name:a.name() ?: (member instanceof Class ? member.simpleName : member.name).capitalize(),
			description:a.description() ?: null,
			fields:[],
			docResources:[],
			snippet: a.snippet() ?: null,
			show: a.show(),
			warnings:warnings(a),
			hints:hints(a),
			sinceVersion:a.sinceVersion() ?: null,
			untilVersion:a.untilVersion() ?: null
		]
		processDocResources(objectDoc, a)
		if (member.isAnnotationPresent(ApiObjectFields)) {
			def a1 = member.getAnnotation(ApiObjectFields)
			a1.value().each {a2->
				objectDoc.fields << buildApiObjectFieldDocFromAnnotation(a2)
			}
		} else if (member instanceof Class) {
			def addAllFields = member.declaredMethods.find{it.isAnnotationPresent(ApiObjectField)} == null && 
				member.declaredFields.find{it.isAnnotationPresent(ApiObjectField)} == null && 
				member.declaredMethods.find{it.isAnnotationPresent(ApiObjectFields)} == null && 
				member.declaredFields.find{it.isAnnotationPresent(ApiObjectFields)} == null
			objectDoc.addAllFields = addAllFields
			if (!addAllFields) {
				member.declaredMethods.each {member2 ->
					if (member2.isAnnotationPresent(ApiObjectFields)) {
						def a1 = member2.getAnnotation(ApiObjectFields)
						a1.value().each {a2->
							objectDoc.fields << buildApiObjectFieldDocFromAnnotation(a2)
						}
					}
					if (member2.isAnnotationPresent(ApiObjectField)) {
						def a1 = member2.getAnnotation(ApiObjectField)
						objectDoc.fields << buildApiObjectFieldDocFromAnnotation(a1, member2.name)
					}
				}
				member.declaredFields.each {member2 ->
					if (member2.isAnnotationPresent(ApiObjectFields)) {
						def a1 = member2.getAnnotation(ApiObjectFields)
						a1.value().each {a2->
							objectDoc.fields << buildApiObjectFieldDocFromAnnotation(a2)
						}
					}
					if (member2.isAnnotationPresent(ApiObjectField)) {
						def a1 = member2.getAnnotation(ApiObjectField)
						objectDoc.fields << buildApiObjectFieldDocFromAnnotation(a1, member2.name)
					}
				}
			} else {
				member.declaredFields.each {member2 ->
					if (!EXCLUDED.contains(member2.name) &&
						!member2.name.startsWith('$') && 
						!member2.name.startsWith('_')) 
						objectDoc.fields << buildApiObjectFieldDocFromField(member2)
				}
			}
		}
		objectDoc
	}

	@SuppressWarnings("rawtypes")
	static buildApiFlowDocFromAnnotatedMember(member) {
		def a = member.getAnnotation(ApiFlow.class)
		def flowDoc = [
			ref:'flow-'+(a.name() ?: member.name),
			category:a.category() ?: '',
			name:a.name() ?: member.name,
			description:a.description() ?: null,
			preconditions:a.preconditions() ?: null,
			warnings:warnings(a),
			hints:hints(a),
			steps:[],
			snippet: a.snippet() ?: null,
			sinceVersion:a.sinceVersion() ?: null,
			untilVersion:a.untilVersion() ?: null
		]
		processDocResources(flowDoc, a)

		a.steps()?.each {paramA->
			def stepDoc = [
				ref:uuid(),
				notes:paramA.notes(),
				methodId:paramA.methodId()
			]
			flowDoc.steps << stepDoc
		}
		flowDoc
	}

	static processUpdates(doc, member) {
		if (member?.isAnnotationPresent(ApiUpdate)) {
			def a = member.getAnnotation(ApiUpdate)
			doc.updates[a.version()] = doc.updates[a.version()] ?: [] 
			doc.updates[a.version()] << a.value()
		}
		if (member?.isAnnotationPresent(ApiUpdates)) {
			def a = member.getAnnotation(ApiUpdates)
			a.value().each {a2->
				doc.updates[a2.version()] = doc.updates[a2.version()] ?: [] 
				doc.updates[a2.version()] << a2.value()
			}
		}

	}

	static addAllControllerURIs(apiDoc, controllerArtefact){
		def uniqueURIs = [] as Set
		controllerArtefact.getURIs().findAll {it.indexOf('*') == -1 && !it.endsWith('/')}.each {uniqueURIs << it}
		def controllerClass = controllerArtefact.clazz
		def controllerName = getLogicalName(controllerClass, 'Controller')
		uniqueURIs.sort()
		uniqueURIs?.each {
			char dlm = '/'
			def parts = it.tokenize(dlm)
			def name = parts.getAt(parts.size()-1).capitalize()
			def methodName = controllerArtefact.getMethodActionName(it)
			def method
			def ok = true
			try {
				method = controllerClass.getMethod(methodName, null)
				ok = !(method.isAnnotationPresent(ApiExcludes))
			} catch(Throwable t) {

			}
			if (ok && name != 'BeforeInterceptor' && name != 'NoSupport' && name != controllerName.capitalize()) {
				def methodDoc = [
					ref:uuid(),
					id:it,
					verbs:['GET'],
					path:it,
					name:name,//getNaturalName(member.name),
					responseCode:200
				]
				if (ADD_API_KEY) {
					def prmDoc = [
						ref:uuid(),
						allowMultiple: false,
						typeInfo: [isList:false, label:'string', type:'string'],
						name:API_KEY_PARAM_NAME,
						defaultValue:'',
						description:'API Key associated with account',
						required:true
					]
					if (ADD_API_KEY_IS_HEADER) {
						methodDoc.headers = [prmDoc]
					} else {
						methodDoc.queryparameters = [prmDoc]
					}
				}
				apiDoc.methods << methodDoc
			}
		}			
	}

	static addAllMethods(apiDoc, serviceClass, artefactType = 'Service'){
		def serviceName = getPropertyName(apiDoc.name ?: getLogicalName(serviceClass.simpleName, artefactType))
		serviceClass.declaredMethods?.each {method->
			String name = method.name
			if (!method.synthetic && !name.startsWith('this') && !name.startsWith('super') && 
				name != 'beforeInterceptor' && name != 'noSupport' &&
				!name.endsWith('Service') && !name.endsWith('Source') && !Modifier.isPrivate(method.getModifiers()) &&
				!Modifier.isStatic(method.getModifiers())) {
				def methodDoc = [
					ref:uuid(),
					id:serviceName+'.'+name,
					verbs:['GET'],
					path:PATH_PREFIX+serviceName+'/'+name,
					name:method.name.capitalize(),//getNaturalName(member.name),
					responseCode:200
				]
				if (ADD_API_KEY) {
					def prmDoc = [
						ref:uuid(),
						allowMultiple: false,
						typeInfo: [isList:false, label:'string', type:'string'],
						name:API_KEY_PARAM_NAME,
						defaultValue:'',
						description:'API Key associated with account',
						required:true
					]
					if (ADD_API_KEY_IS_HEADER) {
						methodDoc.headers = [prmDoc]
					} else {
						methodDoc.queryparameters = [prmDoc]
					}

				}
				apiDoc.methods << methodDoc			
			}

		}			
	}

	static processApiArtefectClass(doc, grailsClass) {
		def addAll = false
		def apiDoc
		if (grailsClass.isAnnotationPresent(Api)) {
			apiDoc = buildApiDocFromAnnotatedMember(grailsClass)
			addAll = grailsClass.declaredMethods.find {member->member.isAnnotationPresent(ApiMethod)} == null
		}
		processUpdates(doc, grailsClass)
		grailsClass.declaredFields.each {member ->
			processUpdates(doc, member)
			if (member.isAnnotationPresent(ApiUpdate)) {
				doc.updates << member.getAnnotation(ApiUpdate).value()
			}
			if (member.isAnnotationPresent(ApiObject)) {
				def objectDoc = buildApiObjectDocFromAnnotatedMember(member)
				doc.objects << objectDoc
			}
			if (member.isAnnotationPresent(ApiFlow)) {
				def flowDoc = buildApiFlowDocFromAnnotatedMember(member)
				doc.flows << flowDoc
			}
			if (member.isAnnotationPresent(ApiMethod)) {
				if (!apiDoc) {
					apiDoc = [ref:uuid(), name:grailsClass.simpleName, category:'', 
						description:'', methods:[]]						
				}
				def methodDoc = buildApiMethodDocFromAnnotatedMember(apiDoc, member)
				apiDoc.methods << methodDoc
			} 
		}			
		grailsClass.declaredMethods.each {member ->
			processUpdates(doc, member)
			if (member.isAnnotationPresent(ApiObject)) {
				def objectDoc = buildApiObjectDocFromAnnotatedMember(member)
				doc.objects << objectDoc
			}
			if (member.isAnnotationPresent(ApiFlow)) {
				def flowDoc = buildApiFlowDocFromAnnotatedMember(member)
				doc.flows << flowDoc
			}
			if (member.isAnnotationPresent(ApiMethod)) {
				if (!apiDoc) {
					apiDoc = [ref:uuid(), name:grailsClass.simpleName, category:'', description:'', methods:[]]						
				}
				def methodDoc = buildApiMethodDocFromAnnotatedMember(apiDoc, member)
				if (!apiDoc.methods.find{it.name == methodDoc.name}) {
					apiDoc.methods << methodDoc
				}
			} 
		}
		if (apiDoc) apiDoc.addAll = addAll
		apiDoc
	}

	@SuppressWarnings("rawtypes")
	static build() {
		def grailsApplication = grails.util.Holders.grailsApplication
		readConfig(grailsApplication)

		def doc = [
			appName: APP_NAME,
			version: VERSION,
			basePath: BASEPATH,
			apiDocTitle: API_DOC_TITLE,
			objectsTitle: OBJECTS_TITLE,
			apisTitle: API_TITLE,
			flowsTitle: FLOWS_TITLE,
			introMessage:INTRO_MESSAGE,
			defaultUpdatesMessage:DEFAULT_UPDATES_MESSAGE,
			playgroundEnabled: PLAYGROUND_ENABLED,
			apiKeyParamName: API_KEY_PARAM_NAME,
			updates:[:],
			apis:[],
			objects:[],
			flows:[]
		]

		if (INTRO_DOC_RESOURCES) {
			INTRO_DOC_RESOURCES.each {r->
				def uri = r.uri
				def resourceType = r.containsKey('resourceType') ? r.resourceType : 'DESCRIPTION_IMAGE'
				def generator = grailsApplication.mainContext['grailsLinkGenerator']	
				if (!uri) {
					uri = generator.link(controller:r.controller ?: null, action:r.action ?: null)
				} else if (resourceType?.endsWith('ASSET')) {
					uri = generator.resource(src:uri)
				}
				doc.docResources = doc.docResources ?: []
				doc.docResources << [type:resourceType.replaceAll('_ASSET', ''), title:r.title,uri:uri, description:r.description]
			}	
		}

		grailsApplication.controllerClasses.each { controllerArtefact ->
			def controllerClass = controllerArtefact.getClazz()
			def apiDoc = processApiArtefectClass(doc, controllerClass)

			if (apiDoc?.addAll) addAllControllerURIs(apiDoc, controllerArtefact)
			if (apiDoc) doc.apis << apiDoc
		}

		grailsApplication.serviceClasses.each { serviceArtefact ->
			def serviceClass = serviceArtefact.getClazz()
			def apiDoc = processApiArtefectClass(doc, serviceClass)

			if (apiDoc?.addAll) addAllMethods(apiDoc, serviceClass)
			if (apiDoc) doc.apis << apiDoc
		}

		grailsApplication.domainClasses.each { domainArtefact ->
			def domainClass = domainArtefact.getClazz()
			processUpdates(doc, domainClass)
			domainClass.declaredFields.each {processUpdates(doc, it)}
			domainClass.declaredMethods.each {processUpdates(doc, it)}
			if (domainClass.isAnnotationPresent(ApiObject)) {
				def objectDoc = buildApiObjectDocFromAnnotatedMember(domainClass)
				doc.objects << objectDoc
			}
		}

		// Add in on the refs to the embedded docs
		doc.objects.each {v->
			v.fields?.each {fld->
				String match = doc.objects.find {v2-> fld.typeInfo?.type == v2.name}?.ref
				if (match) fld.typeInfo.ref = match
			}
		}

		doc.apis.each {v00->
			v00.methods?.each {v->
				String match = doc.objects.find {v2-> v.request?.typeInfo?.type == v2.name}?.ref
				if (match) v.request.typeInfo.ref = match
				if (v.request?.typeInfo?.wrapperObjectType) {
					match = doc.objects.find {v2-> v.request?.typeInfo?.wrapperObjectType == v2.name}?.ref
					if (match) v.request.typeInfo.returnRef = match				
				}

				if (v.request?.typeInfo?.subObjectTypes?.size() > 0) {
					v.request.typeInfo.subObjectTypes?.each {rtnObjType->
						if (rtnObjType) {
							v.request.typeInfo.wrapperObjectTypeInfo = v.request.typeInfo.wrapperObjectTypeInfo ?: []
							match = doc.objects.find {v2-> rtnObjType == v2.name}?.ref
							v.request.typeInfo.wrapperObjectTypeInfo << [ref:match, type:rtnObjType]
						}
					}
				}
				if (v.request?.typeInfo?.subObjectTypes) v.response.typeInfo.remove('subObjectTypes')

				match = doc.objects.find {v2-> v.response?.typeInfo?.type == v2.name}?.ref
				if (match) v.response.typeInfo.ref = match
				if (v.response?.typeInfo?.wrapperObjectType) {
					match = doc.objects.find {v2-> v.response?.typeInfo?.wrapperObjectType == v2.name}?.ref
					if (match) v.response.typeInfo.returnRef = match				
				}
				if (v.response?.typeInfo?.subObjectTypes?.size() > 0) {
					v.response.typeInfo.subObjectTypes.each {rtnObjType->
						if (rtnObjType) {
							v.response.typeInfo.wrapperObjectTypeInfo = v.response.typeInfo.wrapperObjectTypeInfo ?: []
							match = doc.objects.find {v2-> rtnObjType == v2.name}?.ref
							v.response.typeInfo.wrapperObjectTypeInfo << [ref:match, type:rtnObjType]
						}
					}
				}
				//if (v.response?.typeInfo?.subObjectTypes) v.response.typeInfo.remove('subObjectTypes')
			}
		}

	   doc.flows.each {v->
			v.steps?.each {step->
				String match
				doc.apis.each {v1->
					v1.methods.each {v3->
						if (step.methodId == v3.id) match = v3.ref
					}
				}
				if (match) step.methodRef = match
			}
		}		
		println "Doc builder is finished..."
		doc.updates = doc.updates.collect {k, v-> [version:k, messages:v]}
		doc.updates = doc.updates.sort { e1, e2 -> -1 * e1.version.compareTo(e2.version) }
		return doc
	}
}
