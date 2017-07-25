var apiDocObj = {
   "playgroundEnabled": true,
   "apiDocTitle": "Test API",
   "appName": "Api Doc Test",
   "objects": [
      {
         "snippet": null,
         "untilVersion": null,
         "ref": "object-Return",
         "hints": [],
         "warnings": [],
         "name": "Return",
         "show": true,
         "description": "Map returned by API methods",
         "sinceVersion": "1.0",
         "category": "Utilities",
         "fields": [
            {
               "ref": "cqLuDt",
               "typeInfo": {
                  "label": "Boolean",
                  "type": "Boolean",
                  "isList": false,
                  "isMap": false
               },
               "hints": [],
               "warnings": [],
               "name": "success",
               "description": "Success Flag",
               "readOnly": true,
               "sinceVersion": "1.0",
               "required": false
            },
            {
               "ref": "FQJQzb",
               "typeInfo": {
                  "label": "Map",
                  "type": "Map",
                  "isList": false,
                  "isMap": false
               },
               "hints": [],
               "warnings": [],
               "name": "data",
               "description": "Output data (variable)",
               "readOnly": true,
               "sinceVersion": "1.0",
               "required": false
            },
            {
               "ref": "RbKLRm",
               "typeInfo": {
                  "label": "Map",
                  "type": "Map",
                  "isList": false,
                  "isMap": false
               },
               "hints": [],
               "warnings": [],
               "name": "meta",
               "description": "Metadata about results (i.e. total, count)",
               "readOnly": true,
               "sinceVersion": "1.0",
               "required": false
            },
            {
               "ref": "ybWjkl",
               "typeInfo": {
                  "label": "Map",
                  "type": "Map",
                  "isList": false,
                  "isMap": false
               },
               "hints": [],
               "warnings": [],
               "name": "feedback",
               "description": "Map of feedback, typically maps of messages, warnings and errors (key is source, value is code)",
               "readOnly": true,
               "sinceVersion": "1.0",
               "required": false
            }
         ],
         "docResources": []
      },
      {
         "snippet": null,
         "untilVersion": null,
         "ref": "object-Person",
         "hints": ["People are important and good"],
         "warnings": [],
         "name": "Person",
         "show": true,
         "description": "People are just people.",
         "sinceVersion": "1.0",
         "category": "Awesome Stuff",
         "fields": [
            {
               "ref": "pHUhkV",
               "typeInfo": {
                  "label": "string",
                  "type": "string",
                  "isList": false,
                  "isMap": false
               },
               "hints": [],
               "warnings": [],
               "name": "name",
               "description": "Random identifier",
               "readOnly": false,
               "required": true
            },
            {
               "ref": "zFXkLf",
               "typeInfo": {
                  "label": "string",
                  "type": "string",
                  "isList": false,
                  "isMap": false
               },
               "hints": [],
               "warnings": [],
               "name": "description",
               "description": "Detailed description",
               "readOnly": false,
               "required": false
            }
         ],
         "docResources": []
      }
   ],
   "flowsTitle": "Flows",
   "updates": [],
   "version": "0.1",
   "defaultUpdatesMessage": "Check here for the latest information about the API",
   "apisTitle": "APIs",
   "introMessage": "The APIs provide programmatic access to read and write Apidoctest data. This documentatation will help you get going with the APIs as quickly as possible.",
   "basePath": "http://localhost:8080",
   "apis": [{
      "snippet": null,
      "untilVersion": null,
      "ref": "api-Stuff",
      "addAll": false,
      "hints": [],
      "methods": [
         {
            "snippet": null,
            "untilVersion": null,
            "headers": [{
               "ref": "JwUbkL",
               "typeInfo": {
                  "label": "string",
                  "type": "string",
                  "isList": false
               },
               "allowMultiple": false,
               "defaultValue": "",
               "name": "X-API-KEY",
               "show": true,
               "description": "API Key required for API access",
               "required": true
            }],
            "request": null,
            "extraQueryString": true,
            "auth": {"type": "Basic Auth + API Key (HTTP header)"},
            "hints": [],
            "queryparameters": null,
            "warnings": [],
            "description": "Retrieve a list of people",
            "responseCode": "200",
            "path": "/apiDoc/testList/{Object Type}",
            "ref": "method-api-People-List-People",
            "response": {
               "ref": "clkYQh",
               "typeInfo": {
                  "returnRef": "object-Return",
                  "ref": "object-Person",
                  "wrapperObjectType": "Return",
                  "label": "Return with Person",
                  "type": "Person",
                  "isList": true,
                  "isMap": false,
                  "subObjectTypes": []
               },
               "hints": [],
               "warnings": []
            },
            "name": "List People",
            "produces": [
               "application/json",
               "application/xml",
               "text/plain",
               "text/html"
            ],
            "enablePlayground": true,
            "verbs": ["GET"],
            "sinceVersion": "1.0",
            "id": "API_PEOPLE_LIST",
            "consumes": null,
            "pathparameters": [{
               "ref": "fgMzkX",
               "typeInfo": {
                  "label": "string",
                  "type": "string",
                  "isList": false
               },
               "allowMultiple": false,
               "hints": [],
               "warnings": [],
               "name": "Object Type",
               "defaultValue": "Person",
               "show": true,
               "description": "Object Type",
               "required": true
            }],
            "apiErrors": [
               {
                  "untilVersion": null,
                  "ref": "UtPcSO",
                  "code": "400",
                  "hints": [],
                  "warnings": [],
                  "description": "The request is malformed.",
                  "sinceVersion": null
               },
               {
                  "untilVersion": null,
                  "ref": "pukGQY",
                  "code": "401",
                  "hints": [],
                  "warnings": [],
                  "description": "The request is not authorized.",
                  "sinceVersion": null
               },
               {
                  "untilVersion": null,
                  "ref": "SavEYl",
                  "code": "403",
                  "hints": [],
                  "warnings": [],
                  "description": "The request is forbidden.",
                  "sinceVersion": null
               },
               {
                  "untilVersion": null,
                  "ref": "kvWyoo",
                  "code": "500",
                  "hints": [],
                  "warnings": [],
                  "description": "Server error.",
                  "sinceVersion": null
               }
            ]
         },
         {
            "snippet": null,
            "untilVersion": null,
            "headers": [{
               "ref": "OEWEBZ",
               "typeInfo": {
                  "label": "string",
                  "type": "string",
                  "isList": false
               },
               "allowMultiple": false,
               "defaultValue": "",
               "name": "X-API-KEY",
               "show": true,
               "description": "API Key required for API access",
               "required": true
            }],
            "request": {
               "ref": "tZvvTV",
               "typeInfo": {
                  "returnRef": "object-Return",
                  "ref": "object-Person",
                  "wrapperObjectType": "",
                  "label": "Person",
                  "type": "Person",
                  "isList": false,
                  "isMap": false,
                  "subObjectTypes": []
               },
               "hints": [],
               "warnings": []
            },
            "extraQueryString": false,
            "auth": {"type": "Basic Auth + API Key (HTTP header)"},
            "hints": [],
            "queryparameters": null,
            "pathparameters": [{
               "ref": "fgMzkY",
               "typeInfo": {
                  "label": "string",
                  "type": "string",
                  "isList": false
               },
               "allowMultiple": false,
               "hints": [],
               "warnings": [],
               "name": "Object Type",
               "defaultValue": "Person",
               "show": true,
               "description": "Object Type",
               "required": true
            }],
            "warnings": [],
            "description": "Save a new person",
            "responseCode": "202",
            "path": "/apiDoc/testSave/{Object Type}",
            "ref": "method-api-People-Save-Person",
            "response": {
               "ref": "preeMv",
               "typeInfo": {
                  "returnRef": "object-Return",
                  "ref": "object-Person",
                  "wrapperObjectType": "Return",
                  "label": "Return with Person",
                  "type": "Person",
                  "isList": false,
                  "isMap": false,
                  "subObjectTypes": []
               },
               "hints": [],
               "warnings": []
            },
            "name": "Save Person",
            "produces": [
               "application/json",
               "application/xml",
               "text/html"
            ],
            "enablePlayground": true,
            "verbs": ["POST"],
            "sinceVersion": "1.0",
            "id": "API_PERSON_SAVE",
            "consumes": [
               "application/json",
               "application/xml"
            ],
            "apiErrors": [
               {
                  "untilVersion": null,
                  "ref": "NtVJzD",
                  "code": "400",
                  "hints": [],
                  "warnings": [],
                  "description": "Loan data is malformed.",
                  "sinceVersion": null
               },
               {
                  "untilVersion": null,
                  "ref": "NhdDii",
                  "code": "401",
                  "hints": [],
                  "warnings": [],
                  "description": "The request is not authorized.",
                  "sinceVersion": null
               },
               {
                  "untilVersion": null,
                  "ref": "smaiPw",
                  "code": "403",
                  "hints": [],
                  "warnings": [],
                  "description": "The request is forbidden.",
                  "sinceVersion": null
               },
               {
                  "untilVersion": null,
                  "ref": "fRQaCX",
                  "code": "404",
                  "hints": [],
                  "warnings": [],
                  "description": "DMS format not found.",
                  "sinceVersion": null
               },
               {
                  "untilVersion": null,
                  "ref": "GzXOja",
                  "code": "500",
                  "hints": [],
                  "warnings": [],
                  "description": "Server error.",
                  "sinceVersion": null
               }
            ]
         }
      ],
      "warnings": [],
      "name": "People",
      "description": "Methods for importing loans",
      "sinceVersion": "1.0",
      "category": "Interesting Stuff"
   }],
   "objectsTitle": "Objects",
   "flows": [{
      "snippet": null,
      "untilVersion": null,
      "ref": "flow-Checkout People",
      "preconditions": [
         "Auth credentials have been received",
         "Data format is supported"
      ],
      "hints": ["Files can also be imported using the UI"],
      "warnings": [],
      "name": "Checkout People",
      "description": "Import loans from a DMS/LSS export",
      "sinceVersion": "1.0",
      "category": "Crucial Stuff",
      "steps": [
         {
            "ref": "NJyXpo",
            "notes": "Determine loan source type and note endpoint",
            "methodId": "API_PEOPLE_LIST",
            "methodRef": "method-api-People-List-People"
         },
         {
            "ref": "xZcBde",
            "notes": "",
            "methodId": "API_PERSON_SAVE",
            "methodRef": "method-api-People-Save-Person"
         }
      ]
   }],
   "apiKeyParamName": "X-API-KEY",
   "docResources": [
      {
         "description": null,
         "type": "DESCRIPTION_IMAGE",
         "title": "Build your own solution",
         "uri": "/assets/apidoc/image/api.png"
      },
      {
         "description": "Learn more about application programming interfaces.",
         "type": "MORE_INFO_LINK",
         "title": "API Wikipedia Entry",
         "uri": "http://en.wikipedia.org/wiki/Application_programming_interface"
      },
      {
         "description": "Learn more about REST.",
         "type": "MORE_INFO_LINK",
         "title": "REST Wikipedia Entry",
         "uri": "http://en.wikipedia.org/wiki/Representational_state_transfer"
      },
      {
         "description": "Learn more about web services",
         "type": "MORE_INFO_LINK",
         "title": "Web Services Wikipedia Entry",
         "uri": "http://en.wikipedia.org/wiki/Web_service"
      },
      {
         "description": "Learn more about javascript object notation",
         "type": "MORE_INFO_LINK",
         "title": "JSON Wikipedia Entry",
         "uri": "http://en.wikipedia.org/wiki/JSON"
      },
      {
         "description": "Learn more about the Bertram Labs team",
         "type": "MORE_INFO_LINK",
         "title": "BertramLabs",
         "uri": "http://bertramlabs.com/"
      }
   ]
};