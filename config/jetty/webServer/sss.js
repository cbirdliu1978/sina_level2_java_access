chrome.extension.sendMessage({}, function(response) {

	function loadScript(scriptName, callback) {
	    var scriptEl = document.createElement('script');
	    scriptEl.src = chrome.extension.getURL( scriptName );
 	   scriptEl.addEventListener('load', callback, false);
 	   if(document.head){
	  	  document.head.appendChild(scriptEl);
	      console.log(scriptName + ' loaded for ' + location.href);
	    }else{
	      console.log(scriptName + ' load fail for ' + location.href);
	    }
	}
loadScript('src/inject/jquery.js');
loadScript('injectSettings.js');

	var readyStateCheckInterval = setInterval(function() {
  		if (document.readyState === "complete") {
  			clearInterval(readyStateCheckInterval);
		}else{
			return;
		}

		var crtUrl = "" + location.href;
		
		injectVars.forEach(function(v){
			try{
			if(v.urlPattern){
				
				if(crtUrl.indexOf(v.urlPattern) !=-1){
					console.log('find url ' + crtUrl + " for "+ JSON.stringify(v) );
					var _data = v.data;
					_data.forEach(function(vsetter){
						var tag = $(vsetter.finder)[0];
						if(!tag){
							console.log('not find tag with ' + vsetter.finder);
						}else{
							tag.setAttribute(vsetter.attrName, vsetter.attrValue);
						}
					});
				}
			}
			}catch(er__){
				console.log(er__);
			}
		});
		

	}, 10);
});
