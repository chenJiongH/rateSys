<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta name="renderer" content="webkit" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		<title>登录</title>
		<link rel="stylesheet" href="css/login.css"/>
		<script src="js/jquery-3.3.1.min.js"></script>
		<script type="text/javascript">
		$(function() {
			$.post("LoginServlet/getWebSiteHead", function(data){
				if(data.message == "未成功获取标题")
					return ;
				$(".welcome").text(data.message);
			}, 'json')
		})
		</script>
		<script src="js/forbiddenFB.js"></script>
		<script src="js/forbiddenAuto.js"></script>
		<style>
			#test1{
				visibility: hidden;
			}
			#test1 +label{
				width: 18px;
				height: 18px;
				background-color: #f8f8f8;
				border: #dedede solid 1px;
				display: inline-block;
				position: relative;
				top: 2px;
				left: 0px;
				border-radius: 4px;
				cursor: pointer;
				overflow: hidden;
			}
			#test1:checked +label:before{
				content: " ";
				width: 18px;
				height: 18px;
				background: url("images/boxSelected.png") no-repeat;
				background-size: 100% auto;
				display: block;
				color: #ffffff;
				text-align: center;
				font-weight: bolder;
				line-height: 18px;
			}
			#test2{
				visibility: hidden;
			}
			#test2 +label{
				width: 1.8rem;
				height: 1.8rem;
				background-color: #f8f8f8;
				border: #dedede solid 0.1rem;
				display: inline-block;
				position: relative;
				top: 0.2rem;
				left: 0px;
				border-radius: 0.4rem;
				cursor: pointer;
				overflow: hidden;
			}	
			#test2:checked +label:before{
				content: " ";
				width: 1.8rem;
				height: 1.8rem;
				background: url("images/boxSelected.png") no-repeat;
				background-size: 100% auto;
				display: block;
				color: #ffffff;
				text-align: center;
				font-weight: bolder;
				line-height: 1.8rem;
			}
		</style>

		<script type="text/javascript">
		//监控enter键，异步提交表单
	　　 $(document).keydown(function(event){
			if(event.keyCode == 13)
				formSubmit();
   	　　}); 
		
		function reaction(message) {
			console.log("he4re");
			location.replace('CSet.jsp');
		}
		function hid() { 
			console.log("here");
// 			$(nameID).css("visibility", "hidden");
		}
		function formSubmit() {
			if($("#user").val() == "" || $("#pass").val() == "") {
				$("#message").text("用户名和密码不可为空");
				$("#message").css("display", "block");
				setTimeout(function() {$("#message").css("display", "none");}, 2000);
				return ;
			}

			$("#message").text("正在登陆，请稍后...");
			$("#message").css("display", "block");
			$.post("LoginServlet/sub", $("form").serialize(), function(data) {
				console.log(data.message);
				console.log('h33er444e');
				if(data.message.indexOf('登') != -1 || data.message.indexOf('用') != -1 || data.message.indexOf('当') != -1 ) {
					console.log('h33ere');
					$("#message").text(data.message);
					$("#message").css("display", "block");
					setTimeout(function() {$("#message").css("display", "none");}, 3500);
					return ;
				}
				console.log(data.message);
				location.replace(data.message);
			}, 'json')
			
		}
		$(function () {
			if($("#auto")[0]) {
				$("#test1").prop("checked","checked");
				$("form").submit(); 
			} else {
				
			}
			$("#test1").click(function() {
				var rem = document.getElementById("test2");
				rem.checked="checked";
			});
			if($("input[name='password']").prop('value') != "") {
				var rem = document.getElementById("test2");
				rem.checked="checked";
			};
		});
		</script>
		<script>
			function forget() {
				$("#message").text("请联系上级管理员修改密码！");
				$("#message").css("display", "block");
				setTimeout(hid, 3000);
			}
			function hid() {
				$("#message").css("visibility", "none");
			}
		</script>
	</head>
	<body>
	<%
		String username = "";
// 		是否自动显示用户名
		boolean flag = false;
		Cookie[] cs = request.getCookies();
		for(int i = 0; cs != null && i < cs.length; i++) {
			if("rem".equals(cs[i].getName())){
				flag = true;
			} else if("username".equals(cs[i].getName())) {
				username = cs[i].getValue();
			}
		}
		if(!flag) username = "";
	%>
	<div class="wrapper" style="width:190.3rem; height:84rem; margin:0 auto; "> 
		<div class="main">
			<div class="left">
				<div class="welcome">欢迎访问教育厅绩效考核系统</div>
			</div>
			<div class="right">
				<form id='login' action="LoginServlet/sub" method="post">
					<input class="in1" type="text" value="<%=username%>" name="username" id="user"  placeholder="请输入用户名" />
					<input class="in2" type="password" value="" name="password" id="pass"  placeholder="请输入密码" /> <br>
					
<!-- 					<div class="in3"><input type="checkbox" value="auto" name="auto" id="test1"/><label for="test1"></label><label for="test1"><span>自动登录</span></label></div> -->
					<%
						if(flag) {
						
					%>
					<div class="in4"><input type="checkbox" checked="checked" value="rem" name="rem" id="test2"/><label for="test2"></label><label for="test2"><span>记住用户名</span></label></div>
					<%
						} else {
					%>
					<div class="in4"><input type="checkbox" value="rem" name="rem" id="test2"/><label for="test2"></label><label for="test2"><span>记住用户名</span></label></div>
					<%
						}
					%>
					<span class="in6" style="cursor:pointer;" onclick="forget();">忘记密码?</span>
					<span id='message' class='message'></span>
					<input class="in5" type="button" id="" value="安全登录" onclick='formSubmit();'/>
				</form>
			</div>
		</div>
<!-- 		<div class="footer">
			<span>&copy福建省教育厅教学项目评估系统版权所有.&nbsp;&nbsp;转载内容版权归作者及来源网站所有</span>
		</div> -->
	</div>

	</body>
</html>
