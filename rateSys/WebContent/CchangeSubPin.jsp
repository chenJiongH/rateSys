<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head><meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<meta charset="utf-8">
		<title>下级人员密码修改</title>
		<link href="css/changeSubPin.css" rel="stylesheet"/>
		<script src="js/jquery-3.3.1.min.js"></script>
		<script src="js/noneRhead.js"></script>
		<script src="js/getWebSiteHead.js"></script>
		<script src="js/forbiddenFB.js"></script>
		<script src="js/forbiddenAuto.js"></script>
		<script src="js/getWelcomeName.js"></script>
		<script>
		function messageDisplay() {
			$("#message").css("display", "inline-block");
			setTimeout(hid, 3000);
		}
		function hid() {
			$("#message").css("display", "none");
		}
		$(function() {
			$(".in2").click(function() {
				if ($("#checkPass").val() != $("#newPass").val()) {
					$("#message").text("新密码与确认框密码不相同");
					messageDisplay();
					return ;
				} else if($("#name").val() == "" || $("#username").val() == "" || $("#rankName").val() == "") {
					$("#message").text("请输入用户姓名、用户名、用户单位");
					messageDisplay();
					return ;
				} else 
					$("form").submit();
			})
			
		})
		</script>
	</head>
	<body>
	<div class="wrapper" style="width:190rem; height:88rem; margin:0 auto;"> 
		<div class="header">
			<div class='headerContent'>
				<input onclick="window.location.href=('exit.jsp')" type="button" name="" id="" value="退出登录" />			
			</div>
		</div>
		<div class="main">
			<div class="location">当前位置：主页&nbsp;&nbsp;>&nbsp;&nbsp;下级人员密码修改</div>
			<div class="left">
				<!-- <img src="images/hPicture.png" /> -->
				<%
					Cookie[] cs = request.getCookies();
					Cookie c = null;
					String username = "";
					for(int i = 0; cs != null &&  i < cs.length; i++) {
						c = cs[i];
						if("username".equals(c.getName()))
							username = c.getValue();
					}
				%>
				<p style="width: 180px">用户：<%=username %></p>
				<ul>
					<li style="height: 7rem;line-height: 6rem;">
					<span class="s1" style='margin-left: -1rem;display: inline-block;'>欢迎您！</span>
					<span class="s2"><span class='welcomeName'></span></span>
					</li>
					<a href="CexInfoM.jsp"><li><img src="images/left3.png">专家信息</li></a>
					<br>
					<a href="CexGroups.jsp"><li><img src="images/left6.png">建立专家组</li></a>
					<a href="GroupSchoolC.jsp"><li><img src="images/left7.png">专家组学校任务</li></a>
					<a href="CaddMember.jsp"><li><img src="images/left6.png">添加专家组成员</li></a>
					<a href="CexDis.jsp"><li><img src="images/left8.png">专家组任务分配</li></a>
					<br>
					<a href="Cproject_process_fallback.jsp"><li><img src="images/left9.png">项目流程回退</li></a>
					<a href="Cplace-on-file.jsp"><li><img src="images/left9.png">项目评分详情</li></a>
					<a href="#"><li class="now"><img src="images/left2-s.png">下级人员密码修改</li></a>
					<a href="CchangePin.jsp"><li><img src="images/left9.png">修改个人密码</li></a>
				</ul>
			</div>
			<div class="right">
				<form action="ChangeSubPinServlet/change" method="post" style='width:89%'>
					<span class='message' id='message'>提示信息</span>
					输入用户名：<input type="text" name="username" id="username" style='width: 26rem;'>
					<span class="indent">用户单位：</span><input type="text" name="rankName" id="rankName"style='width: 26rem;'>
					<br>
					<span class="indent">用户姓名：</span><input type="text" name="name" id="name"style='width: 26rem;'>
					<br>
					输入新密码：<input type="text" name="newPass" id="newPass"style='width: 26rem;'>
					<span class="indent">确认密码：</span><input type="text" name="" id="checkPass"style='width: 26rem;'>
					<br>
					<input type="button" name="" id="" class="in2" value="确认修改" />
				</form>
			</div>
		</div>
		<div class="footer">
			<span>&copy福建省教育厅教学项目评估系统版权所有.&nbsp;&nbsp;转载内容版权归作者及来源网站所有</span>
		</div>
	</div>
	</body>
</html>
