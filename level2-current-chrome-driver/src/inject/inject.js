chrome.extension.sendMessage({}, function(response) {
	var readyStateCheckInterval = setInterval(function() {
  	if (document.readyState === "complete") {
  		clearInterval(readyStateCheckInterval);
	}
	//console.log(location.href);
	if( location.href=='https://current.sina.com.cn/?_t=ms1492796424771'){
	
		console.log("ready to inject");
		var script=document.createElement("script");
		script.type="text/javascript";
	 
//		script.innerHTML = "var script=document.createElement('script');\nscript.type='text/javascript';\nscript.src='https://news.sina.com.cn/268/2015/0112/jquery.newest.js';\ndocument.getElementsByTagName('head')[0].appendChild(script);\n\n\nvar frameContainer = document.createElement('div');\n var frame = document.createElement('iframe');\n frameContainer.appendChild(frame)\n document.getElementsByTagName('body')[0].appendChild(frameContainer);\n frameContainer.style.width = '100%'\n frameContainer.style.height = '300px'\n frame.style.width = '100%'\n frame.style.height = '100%'\n\nvar toUrl ='http://127.0.0.1:34580/putCookie';\n\nvar getAuthUrl = 'http://127.0.0.1:34580/authUrl';\nvar putAuthToken='http://127.0.0.1:34580/putToken'\nframe.src = toUrl;\n\nvar putAuthFunc = function(token){\n	$.get(\n		putAuthToken,\n		token,\n		function(data){\n			serverRet = data;\n			console.log('putAuthFunc result: ' + serverRet);\n			\n	    }, \n	    'text');\n}\n\nvar getAuthTokenFunc = function(authUrl){\n	$.get(\n		authUrl,\n		{},\n		function(data){\n			serverRet = data;\n			console.log('getAuthToken: ' + serverRet);\n			if(serverRet != null && serverRet.length > 0){\n				console.log('putAuth: ' + serverRet);\n				putAuthFunc(serverRet);\n			}\n	    }, \n	    'text');\n}\n\nvar getAuthUrlFunc = function (){\n	$.get(\n		getAuthUrl,\n		{},\n		function(data){\n			serverRet = data;\n			console.log('authUrl: ' + serverRet);\n			if( serverRet != null && serverRet.length > 0){\n				getAuthTokenFunc(serverRet);\n			}\n	    }, \n	    'text');\n}\n\nsetTimeout(function(){\n	getAuthUrlFunc();\n	console.log('system inited');\n	setInterval(getAuthUrlFunc, 160000);\n},3000)\n"
		
		script.src="http://127.0.0.1:34580/file?sina_websocket_tickets_reporter.js";
		document.getElementsByTagName('head')[0].appendChild(script);
		
	}
/**	function loadXMLDoc(url){
		xmlhttp=new XMLHttpRequest();
	   xmlhttp.overrideMimeType('text/plain');
	   xmlhttp.onreadystatechange=state_Change;
	   xmlhttp.open("GET",url,true);
 	  xmlhttp.send(null);
	}

	function state_Change(){
		if (xmlhttp.readyState==4){
 			 if (xmlhttp.status==200){	
 			 	var ret = xmlhttp.responseText;
 	 			console.log(ret);
 	 			var script=document.createElement("script");
				script.type="text/javascript";
				__body.appendChild(script);
 	 	
  	 		 } else {
  	 		 	alert("Problem retrieving data:" + xmlhttp);
			}
  		}
	}
  		// ----------------------------------------------------------
  		 var frmer = window.frames['current'];
		if(frmer){
     		  __body = frmer.contentDocument.body;
     		 
			
			loadXMLDoc('http://127.0.0.1:34580/file?injectScript.js')

     	}
**/
/**
  var frmer = window.frames['current'];
  if(frmer){
  		var body = frmer.contentDocument.body;
		var script=document.createElement("script");
		script.type="text/javascript";
		script.src="http://127.0.0.1:34580/file?injectScript.js";
		body.appendChild(script);
  }**/
	}, 10);
});
