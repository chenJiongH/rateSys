$(function(){
	$.post("LoginServlet/getWebSiteHead", function(data){
		if(data.message == "未成功获取标题")
			return ;
		var head = "<p>" + data.message + "</p>";
		$(".headerContent").prepend(head);
	}, 'json')
})