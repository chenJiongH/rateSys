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
					<a href="CSet.jsp"><li><img src="images/left1.png">区划和学校</li></a>
					<a href="acM.jsp"><li><img src="images/left1.png">下级账号</li></a>
<!-- 					<a href="changeSubPin.jsp"><li class="now"><img src="images/left2-s.png">下级密码</li></a> -->
					<li style='padding: 0px;height: 1.2rem;'></li>
					
					<a href="pjInfoM.jsp"><li><img src="images/left4.png">评估项目</li></a>
					<a href="indexSet.jsp"><li><img src="images/left5.png">评估指标</li></a>
					<li style='padding: 0px;height: 1.2rem;'></li>
					
					<a href="exInfoM.jsp"><li><img src="images/left3.png">专家信息</li></a>
					<a href="taskM.jsp"><li><img src="images/left7.png">项目学校任务</li></a>
					<a href="exGroups.jsp"><li><img src="images/left6.png">新建专家组</li></a>
					<a href="GroupSchoolP.jsp"><li><img src="images/left7.png">专家组学校任务</li></a>
					<a href="addMember.jsp"><li><img src="images/left6.png">添加专家组成员</li></a>
					<a href="exDis.jsp"><li><img src="images/left8.png">专家组任务分配</li></a>
					<li style='padding: 0px;height: 1.2rem;'></li>
					
					<a href="project_process_fallback.jsp"><li><img src="images/left9.png">项目流程回退</li></a>
					<a href="place-on-file.jsp"><li><img src="images/left9.png">项目评分详情</li></a>
					<a href="changePin.jsp"><li><img src="images/left9.png">个人密码修改</li></a>
				</ul>
			</div>
			<div class="right">
				<form action="ChangeSubPinServlet/change" method="post">
					输入用户名：<input type="text" name="username" id="username">
					<span class="indent">用户单位：</span><input type="text" name="rankName" id="rankName">
					<span class="indent">用户姓名：</span><input type="text" name="name" id="name">
					输入新密码：<input type="text" name="newPass" id="newPass">
					<span class="indent">确认密码：</span><input type="text" name="" id="checkPass">
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
