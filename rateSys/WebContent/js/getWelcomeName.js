$.post("GetWelcomeName/getName", function(data){
	console.log(data);
	$(".welcomeName").text(data);
	$(".left .s1").css("color", "#455fe7");
}, 'json') 