var script=document.createElement("script");
script.type="text/javascript";
script.src="https://news.sina.com.cn/268/2015/0112/jquery.newest.js";
document.getElementsByTagName('head')[0].appendChild(script);


var frameContainer = document.createElement("div");
 var frame = document.createElement("iframe");
 frameContainer.appendChild(frame)
 document.getElementsByTagName('body')[0].appendChild(frameContainer);
 frameContainer.style.width = '100%'
 frameContainer.style.height = '300px'
 frame.style.width = '100%'
 frame.style.height = '100%'

var toUrl ='http://127.0.0.1:34580/putCookie';

var getAuthUrl = 'http://127.0.0.1:34580/authUrl';
var putAuthToken='http://127.0.0.1:34580/putToken'
frame.src = toUrl;

var putAuthFunc = function(token){
	$.get(
		putAuthToken,
		token,
		function(data){
			serverRet = data;
			console.log("putAuthFunc result: " + serverRet);
			
	    }, 
	    "text");
}

var getAuthTokenFunc = function(authUrl){
	$.get(
		authUrl,
		{},
		function(data){
			serverRet = data;
			console.log("getAuthToken: " + serverRet);
			if(serverRet != null && serverRet.length > 0){
				console.log("putAuth: " + serverRet);
				putAuthFunc(serverRet);
			}
	    }, 
	    "text");
}

var getAuthUrlFunc = function (){
	$.get(
		getAuthUrl,
		{},
		function(data){
			serverRet = data;
			console.log("authUrl: " + serverRet);
			if( serverRet != null && serverRet.length > 0){
				getAuthTokenFunc(serverRet);
			}
	    }, 
	    "text");
}

setInterval(getAuthUrlFunc, 120000);