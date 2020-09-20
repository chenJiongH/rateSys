$(function(){
	$.post("ChangePinServlet/getUser", function(data) {
		console.log(data);
		$("#username").val(data.spusername);
		$("#name").val(data.spname);
		$("#username").prop("readonly", true);
		$("#username").css("backgroundColor", "white");
		$("#phono").val(data.phono);
		$("span[class='school']").text(data.sname);
		console.log($("span[class='school']").text());
	}, 'json') 
})