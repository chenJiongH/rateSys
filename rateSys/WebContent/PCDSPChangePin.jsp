<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head><meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<meta charset="utf-8">
		<title>修改个人密码</title>
		<link href="css/exInfoM.css" rel="stylesheet"/>
		<link href="css/PCDSPChangePin.css" rel="stylesheet"/>
		<script src="js/jquery-3.3.1.min.js"></script>
		<script src="js/noneRhead.js"></script>
		<script src="js/getWebSiteHead.js"></script>
		<script src="js/forbiddenFB.js"></script>
		<script src="js/forbiddenAuto.js"></script>
		<script src="js/changePinGetUsernameName.js"></script>
		<script src="js/getWelcomeName.js"></script>
		<script>
		function regCheckName(name) { //检测名称 
			var reg = /^[a-zA-Z\u4e00-\u9fa5]+$/;
			if(!reg.test(name)) 
				return false
			return true;
		}
		function regCheckPhono(phono) { //检测电话
			var reg = /^1\d{10,10}$/;
			if(!reg.test(phono)) 
				return false
			return true;
		}
		function regCheckAge(age) {//检测年龄
			var reg = /^[1-9][0-9]{0,2}$/;
			console.log(reg + " " + age);
			if(!reg.test(age)) 
				return false
			return true;
		}
			$(function() {
				$.post("PCDSPChangePinServlet/findSPMessage", function(data){
					console.log(data);
// 					$("#txt0").val(data.SPUSERNAME);
					$("#txt1").val(data.SPNAME);
					$("#txt2").val(data.SPUSERNAME);
					/* $("#txt3").val(data.SPPASSWORD); */
					$("#txt4").val(data.SPPHONE);
					$("#txt5").val(data.SPORganization);
					$("#txt6").val(data.spspecialty);
					$("#txt7").val(data.spage);
					$("#txt8").val(data.sptitle);
					$("#txt9").val(data.sprank);
					$("#txt10").val(data.spfields);
					$("#txt11").val(data.spgrade);
					$("#txt12").val(data.mid);
				}, 'json')		
			})
			function messageDisplay() {
				$("#message").css("display", "inline-block");
			}
			function hid() {
				$("#message").css("display", "none");
			}
			function changePin() {
				if(!regCheckName($("#txt1").val())){
					$("#message").text("姓名无效");
					messageDisplay();
					setTimeout(hid, 3000);
					$("#txt1").focus();
					return ;
				}
				if(!regCheckPhono($("#txt4").val())){
					$("#message").text("电话无效");
					messageDisplay();
					setTimeout(hid, 3000);
					$("#txt4").focus();
					return ;
				}
				if(!regCheckAge($("#txt7").val())){
					$("#message").text("年龄无效");
					messageDisplay();
					setTimeout(hid, 3000);
					$("#txt5").focus();
					return ;
				}
				if($("#oldPass").val() == "") {
					$("#message").text("原密码不可为空");
					messageDisplay();
					setTimeout(hid, 3000);
					$("#oldPass").focus();
					return ;
				}
				if($("#txt3").val() == "") {
					$("#txt3").val($("#oldPass").val());
				}
				$.post("PCDSPChangePinServlet/spChange",$("form").serialize(),function(data){
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
			<div class="location">当前位置：主页&nbsp;&nbsp;>&nbsp;&nbsp;修改个人密码</div>
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
					<span class="s2"><span class='welcomeName' style='display: flex;width: 120px;'></span>
					</li>
					<%	
						HttpSession session1 = request.getSession();
						String spid = (String) session1.getAttribute("spid");
						String pageLocation = "#"; 
						if(spid != null) { //spid为null，当前账号为校级管理员账号 
							if(spid.charAt(0) == 'D') 						
								pageLocation = "DSPRate.jsp";
							if(spid.charAt(0) == 'C')	
								pageLocation = "CSPRate.jsp";
							if(spid.charAt(0) == 'P')
								pageLocation = "PSPRate.jsp";
						}
					%>
					<a href="<%= pageLocation%>"><li><img src="images/left3.png">绩效评分</li></a>
					<a href="#"><li class="now"><img src="images/left9-s.png">修改密码</li></a>
				</ul>
			</div>
			<div class="right">
				<div class="rhead">
					修改个人密码<span>CHANGE PERSONAL PASSWORD</span>
				</div>
				<form action="ChangePinServlet/change">
					<label id='message' class='message' style=' top: -21%;'>提示消息</label>
					<ul>
<!-- 						spid -->
						<input style="display:none" name="txt0" id="txt0" type="text">
						<li class="first" style='margin-left: 3.5rem;margin-right: 48.3rem;'>原密码：<input autocomplete="off" name="oldPass" id="oldPass" type="text" value=""/></li>
						<li class="first" style='margin-left: 5rem;'>姓名：<input autocomplete="off" name="txt1" id="txt1" type="text" value=""/></li>
						<ol id='mayName'>
<!-- 							<li>123</li><li>123</li><li>123</li>  -->
						</ol>
						<li>账号：<input style='cursor:default' name="txt2" id="txt2" type="text" readonly="readonly"/></li>
						<li style='margin-left: 0.4rem;'>新密码：<input name="txt3" id="txt3" type="password"/></li>
						<li class="first">年龄：<input name="txt7" id="txt7" type="text" /></li>
						<li style='margin-left: 5rem;'>电话：<input name="txt4" id="txt4" type="text" /></li>
						<li>单位：<input name="txt5" id="txt5" type="text" /></li>
						<li class="first">专业：<input name="txt6" id="txt6" type="text" /></li>
						<li>职称：<input name="txt8" id="txt8" type="text" /></li>
						<li>擅长领域：<input name="txt10" id="txt10" type="text" /></li>
						<li class="longtext">学段：<input name="txt11" id="txt11" type="text" /></li>
						<li>职务：<input name="txt9" id="txt9" type="text" /></li>
						<li>级别：<input style='background-color: #fff;border:none' name="txt12" id="txt12" type="text" readonly="readonly" /></li>
					</ul>
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
