$(function(){
	$(".left ul a").click(function() {
		location.replace($(this).prop("href"));
		return false;
	})
})