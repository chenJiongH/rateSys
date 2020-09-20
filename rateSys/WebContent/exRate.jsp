<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head><meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<meta charset="utf-8">
		<title>省级专家评分</title>
		<link href="css/exRat.css" rel="stylesheet"/>
		<script src="js/jquery-3.3.1.min.js"></script>
		<script src="js/noneRhead.js"></script>
		<script src="js/getWebSiteHead.js"></script>
		<script src="js/forbiddenFB.js"></script>
		<script src="js/forbiddenAuto.js"></script>
		<script src="js/getWelcomeName.js"></script>
		<script>
			$(function() {
				$(".first tr:even").css("background", "#f6f6f6");
				$(".second tr:even").css("background", "#f6f6f6");
			})
		</script>
	</head>
	<body>
	<div class="wrapper" style="width:1903px; height:880px; margin:0 auto;">
		<div class="header">
			<div class='headerContent'>
				<input onclick="window.location.href=('exit.jsp')" type="button" name="" id="" value="退出登录" />			
			</div>
		</div>
		<div class="main">
			<div class="location">当前位置：主页&nbsp;&nbsp;>&nbsp;&nbsp;省级专家评分</div>
			<div class="left">
				<!-- <img src="images/hPicture.png" /> -->
				<%
					Cookie[] cs = request.getCookies();
					Cookie c = null;
					String username = ""; 
					for(int i = 0; cs != null &&  i < cs.length; i++) {
						c = cs[i];
						if("username".equals(c.getName())){
							username = c.getValue();
						}
					}
				%>
				<p style="width:180px;">用户：<%=username %></p>
				<ul>
					<li style="height: 7rem;line-height: 6rem;">
					<span class="s1" style='margin-left: -1rem;display: inline-block;'>欢迎您！</span>
					<span class="s2"><span class='welcomeName'></span></span>
					</li>
					<a href="#"><li><img src="images/left2.png">修改密码</a></li>
					<a href="#"><li class="now"><img src="images/left11-s.png">绩效评分</a></li>
				</ul>
			</div>
			<div class="right">
				<div class="rhead">
					省级专家评分<span>Provincial expert rating</span>
				</div>
				<form action="">
					<span class="s1">欢迎你！专家姓名</span><br />
					<span class="s2">福建师范大学</span>
					<input type="button" name="" id="" class="in2" value="确定评分" />
					<div class="indexs">
						<div class="first">
							<span class="h"></span>
							<span class="h2">A指标</span>
							<p>A级指标名1：</p>
							<p>A级指标名1：</p>
						</div>
						<div class="second">
							<span class="h"></span>
							<span class="h2">B指标</span>
							<p>B级指标名1：</p>
							<p>B级指标名1：</p>
							<p>B级指标名1：</p>
							<p>B级指标名1：</p>
						</div>
						<div class="third">
							<span class="h"></span>
							<span class="h2"><span class="h2-2">C指标</span><span class="h2-1">分值</span><span class="h2-3">自评分数</span><span class="h2-3">自评附件</span></span>
							<p class="p1">C级指标名1：<input type="text" name="" id="" /><input type="text" name="" id="" /><input type="button" value="上传"/></p>
							<p class="p2">C级指标名1：<input type="text" name="" id="" /><input type="text" name="" id="" /><input type="button" value="上传"/></p>
							<p class="p1">C级指标名1：<input type="text" name="" id="" /><input type="text" name="" id="" /><input type="button" value="上传"/></p>
							<p class="p2">C级指标名1：<input type="text" name="" id="" /><input type="text" name="" id="" /><input type="button" value="上传"/></p>
							<p class="p1">C级指标名1：<input type="text" name="" id="" /><input type="text" name="" id="" /><input type="button" value="上传"/></p>
							<p class="p2">C级指标名1：<input type="text" name="" id="" /><input type="text" name="" id="" /><input type="button" value="上传"/></p>
							<p class="p1">C级指标名1：<input type="text" name="" id="" /><input type="text" name="" id="" /><input type="button" value="上传"/></p>
							<p class="p2">C级指标名1：<input type="text" name="" id="" /><input type="text" name="" id="" /><input type="button" value="上传"/></p>
						</div>
						<div class="four">
							<span class="h">县（区）级专家</span>
							<span class="h2"><span class="h2-1">分数</span><span class="h2-1">说明</span><span class="h2-1">姓名</span></span>
							<p><input type="text"><input class="cen" type="text"><input type="text"></p>
							<p class="p2"><input type="text"><input class="cen" type="text"><input type="text"></p>
							<p><input type="text"><input class="cen" type="text"><input type="text"></p>
							<p class="p2"><input type="text"><input class="cen" type="text"><input type="text"></p>
							<p><input type="text"><input class="cen" type="text"><input type="text"></p>
							<p class="p2"><input type="text"><input class="cen" type="text"><input type="text"></p>
							<p><input type="text"><input class="cen" type="text"><input type="text"></p>
							<p class="p2"><input type="text"><input class="cen" type="text"><input type="text"></p>
						</div>
						<div class="five">
							<span class="h">市级专家</span>
							<span class="h2"><span class="h2-1">分数</span><span class="h2-1">说明</span><span class="h2-1">姓名</span></span>
							<p><input type="text"><input class="cen" type="text"><input type="text"></p>
							<p class="p2"><input type="text"><input class="cen" type="text"><input type="text"></p>
							<p><input type="text"><input class="cen" type="text"><input type="text"></p>
							<p class="p2"><input type="text"><input class="cen" type="text"><input type="text"></p>
							<p><input type="text"><input class="cen" type="text"><input type="text"></p>
							<p class="p2"><input type="text"><input class="cen" type="text"><input type="text"></p>
							<p><input type="text"><input class="cen" type="text"><input type="text"></p>
							<p class="p2"><input type="text"><input class="cen" type="text"><input type="text"></p>
						</div>
						<div class="six">
							<span class="h">省级专家</span>
							<span class="h2"><span class="h2-1">分数</span><span class="h2-1">说明</span><span class="h2-1">姓名</span></span>
							<p><input type="text"><input class="cen" type="text"><input type="text"></p>
							<p class="p2"><input type="text"><input class="cen" type="text"><input type="text"></p>
							<p><input type="text"><input class="cen" type="text"><input type="text"></p>
							<p class="p2"><input type="text"><input class="cen" type="text"><input type="text"></p>
							<p><input type="text"><input class="cen" type="text"><input type="text"></p>
							<p class="p2"><input type="text"><input class="cen" type="text"><input type="text"></p>
							<p><input type="text"><input class="cen" type="text"><input type="text"></p>
							<p class="p2"><input type="text"><input class="cen" type="text"><input type="text"></p>
						</div>
					</div>
				</form>
				
			</div>
		</div>
		<div class="footer">
			<span>&copy福建省教育厅教学项目评估系统版权所有.&nbsp;&nbsp;转载内容版权归作者及来源网站所有</span>
		</div>
	</div>
	</body>
</html>
