<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head><meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<meta charset="utf-8">
		<title>修改个人密码</title>
		<link href="css/cchangePin.css" rel="stylesheet"/>
		<script src="js/jquery-3.3.1.min.js"></script>
		<script src="js/noneRhead.js"></script>
		<script src="js/getWebSiteHead.js"></script>
		<script src="js/forbiddenFB.js"></script>
		<script src="js/forbiddenAuto.js"></script>
		<script src="js/changePinGetUsernameName.js"></script>
		<script src="js/getWelcomeName.js"></script>
		<script>
			$(function() {
				$(".first tr:even").css("background", "#f6f6f6");
				$(".second tr:even").css("background", "#f6f6f6");
			})
			function messageDisplay() {
				$("#message").css("display", "inline-block");
			}
			function hid() {
				$("#message").css("display", "none");
			}
			function changePin() {
				if($("#username").val() == "" || $("#oldPassword").val() == "" || $("#newPassword").val() == "" ){
					$("#message").text("请填入完整信息再确认修改");
					messageDisplay();
					setTimeout(hid, 3000);
					return ;
				} else if($("#oldPassword").val() == $("#newPassword").val()) {
					$("#message").text("原密码与新密码相同");
					messageDisplay();
					setTimeout(hid, 3000);
					return ;
				}
					var reg = /^1\d{10,10}$/;
					if(!reg.test($("#phono").val())) {
						$("#message").text("电话无效");
						messageDisplay();
						setTimeout(hid, 3000);
						return ;
					}
				$.post("ChangePinServlet/change",$("form").serialize(),function(data){
					if(data.message == "修改成功") {
						location.replace('exit.jsp');
					} else {
						$("#message").text(data.message);
						messageDisplay();
						setTimeout(hid, 3000);
					}
				}, 'json')
			}
		</script>
	</head>
	<body>
	<div class="wrapper" style="width:190.3rem; height:88rem; margin:0 auto;"> 
		<div class="header">
			<div class='headerContent'>
				<input onclick="window.location.href=('exit.jsp')" type="button" name="" id="" value="退出登录" />			
			</div>
		</div>
		<div class="main">
			<div class="location">当前位置：主页&nbsp;&nbsp;>&nbsp;&nbsp;修改个人信息</div>
			<div class="left">
				<!-- <img src="images/hPicture.png" /> -->
				<%
					Cookie[] cs = request.getCookies();
					Cookie c = null;
					String username = ""; 
					for(int i = 0; cs != null && i < cs.length; i++) {
						c = cs[i];
						if("username".equals(c.getName())){
							username = c.getValue();
						}
					}
				%>
				<p style="width:180px;">用户：<%=username %></p>
				<ul>
					<li style="height: 70px;margin-top: 20px;">
					<span class="s1" style='margin-left: -1rem;display: inline-block;'>欢迎您！</span>
					<span class="s2"><span class='welcomeName' style='display: flex;width: 120px;'></span></span>
					</li>
					<%	
						HttpSession session1 = request.getSession();
						String spid = (String) session1.getAttribute("spid");
						String pageLocation = "";
						if(spid != null) { //spid为null，当前账号为校级管理员账号 
							if(spid.charAt(0) == 'D') 						
								pageLocation = "DSPRate.jsp";
							if(spid.charAt(0) == 'C')	
								pageLocation = "CSPRate.jsp";
							if(spid.charAt(0) == 'P')	
								pageLocation = "PSPRate.jsp";
						}
						else 
							pageLocation = "SSPRate.jsp";
					%>
					<a href="<%= pageLocation%>"><li><img src="images/left3.png">绩效评分</li></a>
					<a href="#"><li class="now"><img src="images/left9-s.png">修改用户信息</li></a>
				</ul>
			</div>
			<div class="right">
				<div class="rhead">
					修改个人信息<span>Change personal information</span>
				</div>
				<form action="ChangePinServlet/change">
					<label class='message' id='message'>提示信息</label> 
					用户名：</span><input type="text" name="username" id="username">
					<span style='margin-left: 3rem'>姓名&nbsp;&nbsp;&nbsp;：</span><input type="text" name="name" id="name">
					<span style='margin-left: 3rem'>电话&nbsp;&nbsp;&nbsp;：</span><input type="text" name="phono" id="phono"> <br />
					原密码：<input type="password" name="oldPassword" id="oldPassword">
					<span style='margin-left: 2.8rem'>新密码： </span><input type="password" name="newPassword" id="newPassword">
					<br />
					<input type="button" name="" id="" class="in2" value="确认修改" onclick="changePin()"/>
				</form>
			</div>
		</div>
		<div class="footer">
			<span>&copy福建省教育厅教学项目评估系统版权所有.&nbsp;&nbsp;转载内容版权归作者及来源网站所有</span>
		</div>
	</div>
</body>
</html>
