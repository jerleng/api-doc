function init() {

	// init cliipboard thingy
	$.fn.copyme = function(options) {
		try {
			this.select();
			var jqThis = $(this);
			if (jqThis.is(':input')) 
				jqThis.select(); // form field so select it with jquery
			else { // non-form field so use low-level window API
				var doc = window.document, sel, range, el = jqThis.get(0);
				if (window.getSelection && doc.createRange) {
					sel = window.getSelection();
					range = doc.createRange();
					range.selectNodeContents(el);
					sel.removeAllRanges();
					sel.addRange(range);
				} else if (doc.body.createTextRange) {
					range = doc.body.createTextRange();
					range.moveToElementText(el);
					range.select();
				}
			} 
			jqThis.focus();				
			document.execCommand("copy");
			document.getSelection().removeAllRanges();
			/* 
			// new Clipboard API, doesnt work yet
			var val = jqThis.is(':input') ? jqThis.val() : jqThis.text();
			alert(val);
			var copyEvent = new ClipboardEvent('copy', { dataType: 'text/plain', data: val } );
			document.dispatchEvent(copyEvent);
			*/
			options = options || {};
			if (options.feedbackElement) { // show/hide feedback element
				var dur = options.feedbackDuration || 5000;
				var feedbackElement = $(options.feedbackElement);
				feedbackElement.show().delay(dur).fadeOut();
			}
			if (options.callback) options.callback(this);
		} catch(e) {
			console.log(e);
		}
	};

	// end init clipboard thingy
	// init handlebars
	Handlebars.registerHelper('startsWith', function(str, prefix, ignoreCase, options) {
		if (ignoreCase == true) {
			str = str.toLowerCase();
			prefix = prefix.toLowerCase();
		}
		var result = str.indexOf(prefix) == 0;
		if( result ) {
			return options.fn(this);
		} else {
			return options.inverse(this);
		}
	});		
	Handlebars.registerHelper('contains', function(str, substr, ignoreCase, options) {
		if (ignoreCase == true) {
			str = str.toLowerCase();
			substr = substr.toLowerCase();
		}
		var result = str.indexOf(substr) >= 0;
		if( result ) {
			return options.fn(this);
		} else {
			return options.inverse(this);
		}

	});		
	Handlebars.registerHelper('compare', function(lvalue, rvalue, options) {
		if (arguments.length < 3)
			throw new Error("Handlerbars Helper 'compare' needs 2 parameters");
		operator = options.hash.operator || "==";
		var operators = {
			'==':		 function(l,r) { return l == r; },
			'===':		function(l,r) { return l === r; },
			'!=':		 function(l,r) { return l != r; },
			'<':		function(l,r) { return l < r; },
			'>':		function(l,r) { return l > r; },
			'<=':		 function(l,r) { return l <= r; },
			'>=':		 function(l,r) { return l >= r; },
			'typeof':	 function(l,r) { return typeof l == r; }
		}
		if (!operators[operator])
			throw new Error("Handlerbars Helper 'compare' doesn't know the operator "+operator);
		var result = operators[operator](lvalue,rvalue);

		if( result ) {
			return options.fn(this);
		} else {
			return options.inverse(this);
		}
	});

	Handlebars.registerHelper('equal', function(lvalue, rvalue, options) {
		if(lvalue!=rvalue) {
			return options.inverse(this);
		} else {
			return options.fn(this);
		}
	});

	Handlebars.registerHelper('eachInMap', function ( map, block ) {
		var out = '';
		Object.keys( map ).map(function( prop ) {
			out += block.fn( {key: prop, value: map[ prop ]} );
		});
		return out;
	} );
	
	Handlebars.registerHelper('toString', function (v) { 
	    return '' + v; 
	});
	Handlebars.registerHelper('json', function(context) {
		return JSON.stringify(context, replacer, 2);
	});
	$(document).on('click touch', '.search-group-addon', function() {
		var val 
		$('.query').each(function(idx, itm) {
			val = val || $(itm).val();	
		});
		updateHash('search', val);
	});
	$(document).on('click touch', '.search-group-addon-remove', function() {
		$('.query').val('');
		$("#content").removeHighlight();
	});

	$(document).on('keyup', '.query', function(e) {
		$("#content").removeHighlight();
		var val = $(this).val();
		$('.query').val(val);
		var plain = getPlainQuery(val);
		if (e.which == 13) {
			console.log('enter='+val);
			updateHash('search', val);
			$('#mobile-query').blur();
		} else if (plain && plain.length > 2) {
			$("#content").highlight(plain);
		}
	});

	$(document).on('click','.apidoc-link', function() {
		$('#navbar').removeClass('in');
		updateHash($(this).data('ref'),$(this).data('query'));
	});

	$(document).on('click', '.search-filter', function() {
		var plainQuery = getPlainQuery($(this).data('query'));
		var type = $(this).data('type');
		updateHash('search', (type == 'all' ? plainQuery : type+':'+plainQuery));
	});

	$(document).on('click', '.copy-button', function() {
		var src = $(this).data('source-id');
		var srcJq = $('#'+src);
		var feedbackId = '#'+$(this).data('feedback-id');
		srcJq.copyme({feedbackElement: feedbackId}); // use the jquery plugin installed at top of this file
	});
	$(document).on('click','.open-window-button', function() {
		var thisJq = $(this),
			src = thisJq.data('source-id'),
			title = thisJq.data('title') || 'Agora API',
			srcJq = $('#'+src),
			ct = srcJq.data('content-type'),
			val = srcJq.is(':input') ? srcJq.val() : srcJq.html();
		if (ct != 'text/html') {
			// wrap in HTML tags and style for pretty print markup
			val = '<html><head><style>body {color:#000;font-family:sans-serif;background-color:#ddd}pre{background-color:white;border-radius:5px;padding:10px;}.com{color:#93a1a1;}.lit{ color:#195f91;} .pun,.opn,.clo{color:#93a1a1;}.fun{color:#dc322f;}.str,.atv {color:#D14;}.kwd,.prettyprint .tag{color:#1e347b;}.typ,.atn,.dec,.var{ color:teal;}.pln{color:#48484c;}</style></head><body><h1>'+title+'</h1>'+(ct ? '<h3>Content-Type: '+ct+'</h3>':'')+'<pre>'+val+'</pre></body></html>'
		} else {
			val = srcJq.is(':input') ? srcJq.val() : srcJq.text();
		}
		var newWin = window.open(null);
		newWin.document.write(val);
	});
	$(document).on('click','#playground-max-btn', function() {
		var jqThis = $(this),
			jqContent = $('#content-container'),
			jqTest  = $('#test-container');
		if (jqTest.hasClass('col-md-9')) {
			jqContent.show();
			jqTest.removeClass('col-md-9').addClass('col-md-4').addClass('no-print');
			jqThis.children('i').removeClass('glyphicon-triangle-right').addClass('glyphicon-triangle-left');
		} else {
			jqContent.hide();
			jqTest.removeClass('col-md-4').addClass('col-md-9').removeClass('no-print');
			jqThis.children('i').removeClass('glyphicon-triangle-left').addClass('glyphicon-triangle-right');
		}
	});

	window.applicationCache.addEventListener('updateready', function(e) {
		if (window.applicationCache.status == window.applicationCache.UPDATEREADY) {
			// Browser downloaded a new app cache.
			if (confirm('A new version of this site is available. Load it?')) {
				window.location.reload();
			}
		} else {
			// Manifest didn't changed. Nothing new to server.
		}
	}, false);

}
////////////////////
// global functions
////////////////////

function setContentMode(mode) {
	var jqContent = $('#content-container'),
		jqTest  = $('#test-container');
	if (mode == 'max') {
		jqContent.removeClass('col-md-5').addClass('col-md-9').show();
		jqTest.hide().addClass('no-print')
	} else {
		jqContent.removeClass('col-md-9').addClass('col-md-5').show();
		jqTest.show().removeClass('no-print')
	}
}	
function replacer(key, value) {
	if (value == null) return undefined;
	else return value;
}
function apiObjectToTemplate(o, includeReadOnly, stack) {
	includeReadOnly = includeReadOnly != undefined ? includeReadOnly : true;
	stack = stack || [];
	if (stack.indexOf(o.ref) > -1 || stack.length > 2) return {}
	if (o.ref) stack.push(o.ref)
	var rtn = {};
	$.each(o.fields, function(idx, itm) {
		var defaultValue,type = "string";
		if (includeReadOnly || itm.readOnly != true) {
			if (itm.typeInfo && itm.typeInfo.type) type = itm.typeInfo.type.toLowerCase();
			//console.log(type);
			if (type == "int" || type == "integer" || type == "byte" || type == 'long') 
				defaultValue = itm.defaultValue ? parseInt(itm.defaultValue) : 0;
			else if (type == "float" || type == "double" || type == "decimal" || type == "bigdecimal") 
				defaultValue = itm.defaultValue ? parseFloat(itm.defaultValue) : 0.0;
			else if (type == "boolean")
				defaultValue = itm.defaultValue ? itm.defaultValue.toLowerCase() == "true" : false ;
			else if (type == "date")
				defaultValue = itm.defaultValue ? new Date(itm.defaultValue) : new Date(); 
			else if (type == "map")
				defaultValue = {key:itm.defaultValue || "field value"};
			else if (itm.typeInfo && itm.typeInfo.ref) {
				var o2 = jlinq.from(plainObjects).equals("ref", itm.typeInfo.ref).first();
				if (o2) {
					defaultValue = apiObjectToTemplate(o2, includeReadOnly, stack.slice(0))
				} else {
					defaultValue = "[object]"
				}
			}
			else 
				defaultValue = itm.defaultValue || "";
			if (itm.typeInfo && itm.typeInfo.isList) {
				rtn[itm.name] = defaultValue != null ? [defaultValue]: [];
			} else if (itm.typeInfo && itm.typeInfo.isMap) {
				rtn[itm.name] = defaultValue != null ? {fieldName: defaultValue}: {fieldName: "field value"};
			} else {
				rtn[itm.name] = defaultValue
			}
		}
	});
	return rtn
}

function fillBasicAuthFields() {
	$("#basicAuthPassword").val($("#basicAuthSelect").val());
	$("#basicAuthUsername").val($("#basicAuthSelect").find(":selected").text());
}

function printResponse(data, res, url) {
	//console.log(res);
	var ct = res.getResponseHeader('Content-Type') || 'application/json';
	ct = ct.split(';')[0];
	$("#response").data('content-type', ct)
	if(ct.indexOf('text/xml') == 0) {
		$("#response").text(formatXML(res.responseText));
	} else if(ct.indexOf('application/json') == 0 || 
		ct.indexOf('text/json') == 0) {
		$("#response").text(JSON.stringify(data, undefined, 2));
	} else {
		$("#response").text(res.responseText);
	}
	$("#responseStatus").text(res.status);
	$("#responseHeaders").text(res.getAllResponseHeaders());
	$("#requestURL").text(url);
	$('#testButton').button('reset');
	$('#resInfo .nav-tabs a:first').tab('show') 
	$("#resInfo").show();
	$('#response').removeClass('prettyprinted');
	prettyPrint();
}

function formatXML(xml, indentStr) {
	indentStr = indentStr || '  '
	var formatted = '';
	var reg = /(>)(<)(\/*)/g;
	xml = xml.replace(reg, '$1\r\n$2$3');
	var pad = 0;
	jQuery.each(xml.split('\r\n'), function(index, node) {
		var indent = 0;
		if (node.match( /.+<\/\w[^>]*>$/ )) {
			indent = 0;
		} else if (node.match( /^<\/\w/ )) {
			if (pad != 0) {
				pad -= 1;
			}
		} else if (node.match( /^<\w[^>]*[^\/]>.*$/ )) {
			indent = 1;
		} else {
			indent = 0;
		}
		var padding = '';
		for (var i = 0; i < pad; i++) {
			padding += 'ejsj';
		}
		formatted += padding + node + '\r\n';
		pad += indent;
	})
	formatted = formatted.replace(/ejsj/g, indentStr);
	return formatted;
}

function buildTestContent(method) {

	var testHTML = testTemplate(method);
	$("#testContent").html(testHTML);
	$('[data-source-id="response"]').attr('data-title', method.name+ ' Response');

	$("#testContent").show();

	// if request is not empty then put jsondocTemplate into textarea
	if(method.request && method.request.typeInfo && method.request.typeInfo.ref) {
		var o = jlinq.from(plainObjects).equals("ref", method.request.typeInfo.ref).first();
		var obj = apiObjectToTemplate(o, false);
		$("#inputJson").text(JSON.stringify(obj, replacer, 2));	
	}
	
	$("#produces input:first").attr("checked", "checked");
	$("#consumes input:first").attr("checked", "checked");
	// look in local storage to prepop values
	try {
		for (var i = 0; i < localStorage.length; i++){
			var k = localStorage.key(i);
			console.log('Looking at '+k+' comparing to '+method.ref);
			if (k.indexOf(method.ref) == 0 || k.indexOf('~~') == 0) {
				var keyParts = k.split('|'),
					m = keyParts[0],
					n = keyParts[1],
					t = keyParts[2]
					s = 'input[name="'+n+'"]',
					v = localStorage.getItem(k);
				console.log('Found '+v);
				if (m != '~~') s += '[data-method-ref="'+m+'"]';
				if (t != '~~') s += '[data-parameter-type="'+t+'"]';
				console.log('selecting '+s);
				$(s).val(v);
			}
		}
	} catch (e) {
		console.log(e);
	}
	//console.log($('#basePathField').val());
	if ($('#basePathField').val()) {
		//$('#basePath').html($('#basePathField').val());
	} else {
		//$('#basePath').html(model.basePath);
		$('#basePathField').val(model.basePath);
	}
	// store typed values for prepopulating later
	$(".parameter-field").on('keyup',function() {
		try {
			var jqThis = $(this),
				m = jqThis.data('method-ref') || '~~',
				t = jqThis.data('parameter-type') || '~~',
				n = jqThis.attr('name'),
				v = jqThis.val();
			console.log(m+'|'+n+'|'+t+'='+v);
			localStorage.setItem(m+'|'+n+'|'+t, v);
		} catch(e) {
			console.log(e);
		}
	});

	$("#testButton").click(function() {
		var headers = new Object();
		$("#headers input").each(function() {
			headers[this.name] = $(this).val();
		});
		
		headers["Accept"] = $("#produces input:checked").val();
		if(method.auth) {
			if(method.auth.type.toLowerCase().indexOf("basic auth") >=0 ) {
				headers["Authorization"] = "Basic " + window.btoa($('#basicAuthUsername').val() + ":" + $('#basicAuthPassword').val());
			}
		}
		var replacedPath = method.path;
		var tempReplacedPath = replacedPath; // this is to handle more than one parameter on the url
		
		var validationErrors = [];
		$('#validationerrors').hide();
		$('#validationerrors ul').empty();
		
		$("#pathparameters input").each(function() {
			$('#' + this.id).parent().removeClass('has-error');
			
			if($(this).val()) {
				tempReplacedPath = replacedPath.replace("{"+this.name+"}", $(this).val());
				replacedPath = tempReplacedPath;	
			} else {
				validationErrors.push(this.name + ' must not be empty');
				$('#' + this.id).parent().addClass('has-error');
			}
		});

		var queryParametersMap = {};
		$("#queryparameters input").each(function() {
			$('#' + this.id).parent().removeClass('has-error');

			if($(this).val()) {
				queryParametersMap[this.name] = $(this).val();
			} else {
				if($(this).attr("required")) {
					validationErrors.push(this.name + ' must not be empty');
					$('#' + this.id).parent().addClass('has-error');
				}
			}
		});
		
		var encodedQueryParameters = $.param(queryParametersMap);
		if(encodedQueryParameters) {
			replacedPath = replacedPath + "?" + encodedQueryParameters; 
		}
		var extraQueryString = $('#extraQueryString').val();
		if (extraQueryString) {
			replacedPath += (encodedQueryParameters ? extraQueryString : ("?"+extraQueryString))
		}
		
		if(validationErrors.length > 0) {
			for (var k=0; k<validationErrors.length; k++) {
				$('#validationerrors ul').append($('<li/>').text(validationErrors[k]));
				
			}
			$('#validationerrors').show();
			validationErrors = [];
			return;
		}

		$('#testButton').button('loading');
		
		var headerTxt = '';
		$.each(headers, function(k,v){
			headerTxt += (k+': '+v+'\n');
		});
		$('#requestHeaders').text(headerTxt);

		var res = $.ajax({
			url : ($('#basePathField').val() || model.basePath) + replacedPath,
			type: method.verbs && method.verbs.length > 0  ? method.verbs[0] : 'GET',
			data: $("#inputJson").val(),
			headers: headers,
			contentType: $("#consumes input:checked").val(),
			success : function(data) {
				printResponse(data, res, this.url);
			},
			error: function(data) {
				printResponse(data, res, this.url);
			}
		});
		
	});
}

function getUrlParameter(prmName) {
	var loc = window.location.hash.substr(1);
	//var loc = window.location.search.substring(1);
	var qry = loc.split('&');
	for (var i = 0; i < qry.length; i++)  {
		var prm = qry[i].split('=');
		if (prm[0] == prmName) return decodeURIComponent(prm[1]);
	}
}

function toStringExt(itm) {
	if (typeof itm === 'object') {
		var rtn = '';
		for (prop in itm) {
			rtn += toStringExt(itm[prop]);
		}
		return rtn;
	} else if (itm instanceof Array) {
		var rtn = '';
		$.each(itm, function(idx, mbr) {
			rtn += toStringExt(mbr);
		});
		return rtn;
	} else {
		return itm.toString();
	}
}

function getPlainQuery(q) {
	if (q.indexOf(':')) {
		q = q.substring(q.indexOf(':')+1);
	}
	return q;
}

function getTypeQuery(q) {
	var type
	if (q.indexOf(':')) {
		type = q.substring(0, q.indexOf(':'));
	}
	return type;
}

function search(query) {
	//console.log(query);
	var results = [];
	var types = ['Api', 'Method', 'Object', 'Flow'];
	var targetArray = -1;
	var plainQuery = getPlainQuery(query);
	var type = getTypeQuery(query);
	if (type) {
		for (var j = 0; j < types.length; j++) {
			if (type.toLowerCase() == types[j].toLowerCase()) {
				targetArray = j;
			}
		}
	}
	$.each([plainApis, plainMethods, plainObjects, plainFlows], function(i, arr) {
		if (arr && (targetArray === -1 || targetArray == i)) {
			$.each(arr, function(idx, itm) {
				var stringifiedValue = toStringExt(itm);
				if (stringifiedValue.search(new RegExp(plainQuery, 'i')) > -1) {
					var result = {
						type: types[i],
						name: itm.name,
						description: itm.description,
						ref: itm.ref
					};
					//console.log('type: ' +result.type+', name: '+result.name+', ref:'+result.ref);
					results.push(result);
				}
			});
		}
	});
	results.sort(function(a, b) {return a.name.localeCompare(b.name)});
	return results;
}

function showApiDocItem(ref, query) {
	var content = $("#content");
	var testContent = $("#testContent");
	if (query) $('.query').val(query);
	else {
		$('.query').each(function(idx, itm) {
			query = query || $(itm).val();	
		});
	}
	$('#navbar').removeClass('in');
	setContentMode('max');
	if (ref == 'intro') {
		var introHTML = introTemplate(docTree);
		content.html(introHTML);
		testContent.hide();
		prettyPrint();
		$('html,body').scrollTop(0);
		document.title = model.appName + ' API';
		return false;
	}
	else if (ref == 'search') {
		var searchHTML;
		query = query != undefined ? query : getUrlParameter('query');
		if (query) {
			var results = search(query);				
			searchHTML = searchTemplate({query:query,results:results, size:results.length});
		} else {
			searchHTML = searchTemplate({query:"",results:[], size:0});
		}
		content.html(searchHTML);
		if (query) content.highlight(getPlainQuery(query));
		testContent.hide();
		$('.query').val(query);
		$('html,body').scrollTop(0);

		$('.search-filter').removeClass('active');
		var type = getTypeQuery(query);
		if (type) $('#'+type+'-search-filter').addClass('active');
		else $('#all-search-filter').addClass('active');
		document.title = model.appName + ' API - Search Results';
		return false;
	}

	var template, itemType;
	var showTest = false;
	var attName = 'data-ref';
	var item = jlinq.from(plainApis).equals("ref", ref).first();
	if (item) {
		template = apiTemplate;
		itemType = 'api';
	} 
	if (!item) {
		item = jlinq.from(plainMethods).equals("ref", ref).first();
		if (item) {
			template = methodTemplate;
			itemType = 'method';
		} 
	}
	if (!item) {
		item = jlinq.from(plainObjects).equals("ref", ref).first();
		if (item) {
			template = objectTemplate;
			itemType = 'object';
		} 
	}
	if (!item) {
		item = jlinq.from(plainFlows).equals("ref", ref).first();
		if (item) {
			template = flowTemplate;
			itemType = 'flow';
		}
	}
	if (!item) {
		updateHash('intro');
		return;
	}
	testContent.hide();
	if (itemType == 'object') {
		var objectExample= apiObjectToTemplate(item, true)
		var testHTML = objectExampleTemplate(objectExample);
		testContent.html(testHTML);
		$('[data-source-id="object-template"]').attr('data-title', item.name+ ' Object Template');
		setContentMode('normal');
		testContent.show();
	} else if (itemType == 'method' && item.enablePlayground != false) {
		buildTestContent(item);
		if (isOffline) {
			setContentMode('max');
		} else {
			setContentMode('normal');
		}
		testContent.show();
	}
	var html = template(item);
	content.html(html);
	$('#version').html(model.version);
	if (query) content.highlight(getPlainQuery(query));
	content.show();
	prettyPrint();
	$('html,body').scrollTop(0); 
	document.title = (item.apiName ? item.apiName+'.':'')+ item.name;
	//event.stopPropagation();
	return item;
}

function processDocResources(origDoc) {
	if (origDoc.docResources) {
		origDoc.descriptionImages = [];
		origDoc.screenshots = [];
		origDoc.demoLinks = [];
		origDoc.moreInfoLinks = [];
		origDoc.downloads = [];
		origDoc.authorLinks = [];
		$.each(origDoc.docResources, function(idx, obj) {
			if (obj.type == 'SCREENSHOT') origDoc.screenshots.push(obj)
			else if (obj.type == 'DESCRIPTION_IMAGE') origDoc.descriptionImages.push(obj)
			else if (obj.type == 'DEMO_LINK') origDoc.demoLinks.push(obj)
			else if (obj.type == 'MORE_INFO_LINK') origDoc.moreInfoLinks.push(obj)
			else if (obj.type == 'DOWNLOAD') origDoc.downloads.push(obj)
			else if (obj.type == 'AUTHOR_LINK') origDoc.authorLinks.push(obj)
		});
	}
}

function buildFromJSONDoc(data) {
	model = data;
	docTree.doc = data;
	processDocResources(model);
	if(data.apis && data.apis.length > 0) {
		// this builds an plain array out of the apis map, that makes selecting with jlinq much easier
		plainApis = data.apis;
		$.each(plainApis, function(i,p) {
			p.apiType = 'API'
			processDocResources(p);
			var category = p.category || "";
			docTree.apis[category] = docTree.apis[category] != undefined ? docTree.apis[category] : [];
			docTree.apis[category].push(p);
			$.each(p.methods, function(k, q){
				q.apiType = 'Method'
				q.apiName = p.name;
				q.apiRef = p.ref;
				q.apiDescription = p.description;
				processDocResources(q);
				plainMethods.push(q);
			});
		});
		$("#sidenav-apis").show();
	} else {
		$("#sidenav-apis").hide();
	}
	
	if(data.objects && data.objects.length > 0) {
		plainObjects = data.objects;
		$.each(plainObjects, function(i,p) {
			if (p.show) {
				p.apiType = 'Object'
				var category = p.category || "";
				docTree.objects[category] = docTree.objects[category] != undefined ? docTree.objects[category] : [];
				processDocResources(p);
				docTree.objects[category].push(p);
			}
		});
		$("#sidenav-objects").show();
	} else {
		$("#sidenav-objects").hide();
	}
	if(data.flows && data.flows.length > 0) {
		plainFlows = data.flows;
		$.each(plainFlows, function(i,p) {
			p.apiType = 'Flow'
			processDocResources(p);
			var category = p.category || "";
			docTree.flows[category] = docTree.flows[category] != undefined ? docTree.flows[category] : [];
			docTree.flows[category].push(p);
			p.methods = [];
			$.each(p.steps, function(k, o) {
				var method = jlinq.from(plainMethods).equals("ref", o.methodRef).first();
				//	method.name = 'Step '+(k+1) + ': '+method.name;
				// Deep copy
				var newMethod = $.extend(true, {}, method);
				newMethod.notes = o.notes

				p.methods.push(newMethod);
			});
		});
		$("#sidenav-flows").show();
	} else {
		$("#sidenav-flows").hide();
	}

	// display sidebar
	$.each(docTree.apis, function(k,v) {
		v.sort(function(a, b){
			if (a.name > b.name) return 1;
			if (a.name < b.name) return -1;
			return 0;
		})
	});
	$.each(docTree.objects, function(k,v) {
		v.sort(function(a, b){
			if (a.name > b.name) return 1;
			if (a.name < b.name) return -1;
			return 0;
		})
	});
	$.each(docTree.flows, function(k,v) {
		v.sort(function(a, b){
			if (a.name > b.name) return 1;
			if (a.name < b.name) return -1;
			return 0;
		})
	});
	//console.log(docTree);
	var apisHTML = apisTemplate(docTree);
	$("#apidiv").html(apisHTML);
	var mobileNavHTML = mobileNavTemplate(docTree);
	$("#navbar").html(mobileNavHTML);
	var objectsHTML = objectsTemplate(docTree);
	$("#objectdiv").html(objectsHTML);
	var flowsHTML = flowsTemplate(docTree);
	$("#flowdiv").html(flowsHTML);
	$('.intro-link').html(model.apiDocTitle);
	$('meta[name="apple-mobile-web-app-title"]').attr('content', model.apiDocTitle);
	$('#objects-title').html(model.objectsTitle);
	$('#apis-title').html(model.apisTitle);
	$('.query').attr('placeholder','Search '+model.apiDocTitle);
	$('#flows-title').html(model.flowsTitle);
	$('#side-accordion').show();
}

function updateHash(ref, query) {
	window.location.hash = 'ref='+ref+(query?'&query='+query:'');
}

function handleHashUpdate() {
	//console.log(window.location.hash);
	var ref = getUrlParameter('ref');
	$('.query').val(getUrlParameter('query'));
	var item = showApiDocItem(ref || 'intro');
	if (item) $('.'+(item.apiRef || item.ref)).collapse('show');		
}
function processDoc(data) {
	buildFromJSONDoc(data);
}
////////////////////
// end global functions
////////////////////

// GLOBAL VARS
var model;
var isOffline = !navigator.onLine;
//var jsondoc = JSON.stringify('_JSONDOC_OFFLINE_PLACEHOLDER_');
var introTemplate, 
	searchTemplate, 
	mainTemplate, 
	apisTemplate, 
	objectsTemplate, 
	apiTemplate, 
	methodTemplate, 
	objectTemplate, 
	objectExampleTemplate, 
	flowsTemplate, 
	flowTemplate, 
	testTemplate;
var plainApis = [], 
	plainObjects = [], 
	plainMethods = [], 
	plainFlows = [];
var docTree = {apis:{},objects:{},flows:{}};
// END GLOBAL VARS


requirejs.config({
    baseUrl: '/assets/apidoc/script',
    paths: {},
	shim: {
		'apidoc-bootstrap': {
			deps: [ 'apidoc-jquery-1.11.1' ]
		},
		'apidoc-handlebars-v2.0.0': {
			exports: 'Handlebars'
		},
		'apidoc-jlinq': {
			exports: 'jlinq'
		},
		'apidoc-prettify': {
			exports: 'prettyPrint'
		},
		'apidoc-highlight': {
			deps: [ 'apidoc-jquery-1.11.1' ]
		},
		'/api/doc/data/jsonv': {
			deps: [ 'apidoc-jquery-1.11.1' ],
			exports: 'apiDocObj'
		}
	}
});

require([
	"domReady!",
	"apidoc-jquery-1.11.1",
	"apidoc-bootstrap",
	"apidoc-handlebars-v2.0.0",
	"apidoc-jlinq",
	"apidoc-prettify",
	"apidoc-highlight",
	"/api/doc/data/jsonv",
	"text!template/api.hbs",
	"text!template/apiHeader.hbs",
	"text!template/apis.hbs",
	"text!template/flow.hbs",
	"text!template/flows.hbs",
	"text!template/intro.hbs",
	"text!template/method.hbs",
	"text!template/mobileNav.hbs",
	"text!template/object.hbs",
	"text!template/objects.hbs",
	"text!template/objectTemplate.hbs",
	"text!template/parameterRowTemplate.hbs",
	"text!template/resourcesPartial.hbs",
	"text!template/search.hbs",
	"text!template/test.hbs"
],
    function(doc, $, bs, hb, jl, pr, hi, ado, api, apiHeader, apis, flow, flows, intro, method, mobileNav, object, objects, objectTemplatePrm, parameterRowTemplate, resourcesPartial, search,test) {
    	window.onhashchange = handleHashUpdate;
		init();
		Handlebars.registerPartial("apis", apis);		
		Handlebars.registerPartial("objects", objects);
		Handlebars.registerPartial("mobileNav", mobileNav);
		Handlebars.registerPartial("flows", flows);
		Handlebars.registerPartial("apiHeader", apiHeader);
		Handlebars.registerPartial("method", method);
		Handlebars.registerPartial("parameterRowTemplate", parameterRowTemplate);	
		Handlebars.registerPartial("resources", resourcesPartial);
		introTemplate = Handlebars.compile(intro);
		searchTemplate = Handlebars.compile(search);
		apisTemplate = Handlebars.compile(apis);
		mobileNavTemplate = Handlebars.compile(mobileNav);
		objectsTemplate = Handlebars.compile(objects);
		flowsTemplate = Handlebars.compile(flows);
		flowTemplate = Handlebars.compile(flow);
		apiTemplate = Handlebars.compile(api);
		methodTemplate = Handlebars.compile(method);
		objectTemplate = Handlebars.compile(object);
		objectExampleTemplate = Handlebars.compile(objectTemplatePrm);
		testTemplate = Handlebars.compile(test);
		try {
			if (!apiDocObj) {
				// Retrieve the object from storage
				console.log('Using previously stored apiDocObj')
				apiDocObj = JSON.parse(localStorage.getItem('apiDocObj'))
			} else {
				console.log('Storing new apiDocObj')
				localStorage.setItem('apiDocObj', JSON.stringify(apiDocObj));
			}
		} catch (e) {
			console.log('Unable to use localStorage for the apiDocObj');
			console.log(e);
		}

		if (apiDocObj) {
			processDoc(apiDocObj);
			handleHashUpdate();
		} else {
			alert('API data not found');
		}
	}
);	