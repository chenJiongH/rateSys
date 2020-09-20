var manaTid = "";
$.post("ProjectProcessFallback/getUser", function(data) {
	console.log(data);
	manaTid = data.tid 
}, 'json') 
