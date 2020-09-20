<%@ page language="java" import="java.util.List,domain.CityCountySchool,com.fasterxml.jackson.databind.ObjectMapper,com.fasterxml.jackson.core.type.TypeReference"
	contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>导出信息</title>
		<link href="css/deriInfo.css" rel="stylesheet"/>
		<script src="js/jquery-3.3.1.min.js"></script>
		<script src="js/noneRhead.js"></script>
		<script src="js/forbiddenFB.js"></script>
		<script src="js/forbiddenAuto.js"></script>
		<script src="js/getWelcomeName.js"></script>
		<script>
			$(function() {
				$(".first tr:even").css("background", "#f6f6f6");
				$(".second tr:even").css("background", "#f6f6f6");
			})
			$(function() {
				$("input").click(function() {
					$("form").submit();
				})				
			})
		</script>
	</head>
	<body>
	<div class="wrapper" style="width:1903px; height:880px; margin:0 auto;">
		<div class="header">
			<p>福建省教育厅教学<span>项目评估系统</span></p>
			<input onclick="window.location.href=('exit.jsp')" type="button" name="" id="" value="退出登录" />
		</div>
		<div class="main">
			<div class="location">当前位置：主页&nbsp;&nbsp;>&nbsp;&nbsp;导出信息</div>
			<div class="left">
				<!-- <img src="images/hPicture.png" /> -->
				<%
					Cookie[] cs = request.getCookies();
					Cookie c1 = null;
					String username = ""; 
					for(int i = 0; cs != null &&  i < cs.length; i++) {
						c1 = cs[i];
						if("username".equals(c1.getName())){
							username = c1.getValue();
						}
					}
				%>
				<p style="width:180px;">用户：<%=username %></p>
				<ul>
					<li style="height: 36px;">
					<span class="s1" style='margin-left: -10px;vertical-align: top;display: inline-block;'>欢迎您！</span>
					<span class="s2"><span class='welcomeName'></span></span>
					</li>
					<a href="acM.jsp"><li><img src="images/left1.png">下级账号管理</li></a>
					<a href="changeSubPin.jsp"><li><img src="images/left2.png">下级人员密码修改</li></a>
					<a href="exInfoM.jsp"><li><img src="images/left3.png">专家信息管理</li></a>
					<a href="pjInfoM.jsp"><li><img src="images/left4.png">项目信息管理</li></a>
					<a href="indexSet.jsp"><li><img src="images/left5.png">指标设置</li></a>
					<a href="CSet.jsp"><li><img src="images/left1.png">市县校区设置</li></a>
					<a href="exGroups.jsp"><li><img src="images/left6.png">建立专家组</li></a>
					<a href="addMember.jsp"><li><img src="images/left6.png">添加专家组成员</li></a>
					<a href="taskM.jsp"><li><img src="images/left7.png">专家组、学校任务管理</li></a>
					<a href="exDis.jsp"><li><img src="images/left8.png">组内专家、指标管理</li></a>
					<a href="changePin.jsp"><li><img src="images/left9.png">修改个人密码</li></a>
					<a href="#"><li class="now"><img src="images/left10-s.png">导出信息</li></a>
				</ul>
			</div>
			<div class="right">
				<div class="rhead">
					导出信息<span>Export information</span>
				</div>
				
				<form action="ExcelExport" method="post">
					<span>数据预览：</span>
					<input type="button" name="" id="" class="in2" value="导出EXCEL" />
				</form>
				<p style="margin-left: 50px;">市</p>
				<p>县区</p>
				<p class="p3">学校</p>
				<p class="p3"><span class="s1">类别</span></p>
				<div class="tdiv">
					<table>
					<%
					// 	在ExcelExportSmall中创建的session，保存了CSet.jsp中的json数据
					try{
						String json = (String)session.getAttribute("json");
						ObjectMapper mapper = new ObjectMapper();
// 						System.out.println(1234);
						//将json字符串转换为java Bean对象数组
						List<CityCountySchool> cs1 = mapper.readValue(json,  new TypeReference<List<CityCountySchool>>() {});
// 						System.out.println(cs);						
						for(CityCountySchool c : cs1) {
					%>	
					<tr>
					<%
						if(c.getCname() == null)
							c.setCname("");
					%>
						<td class='t1'><%=c.getCname() %></td>
					<%
					if(c.getDname() == null)
						c.setDname("");
					%>
						<td class='t2'><%=c.getDname() %></td>
					<%
					if(c.getSname() == null)
						c.setSname("");
					%>
						<td class='t3'><%=c.getSname() %></td>
					<%
					if(c.getType() == null)
						c.setType("");
					%>
						<td class='t4'><%=c.getType() %></td>
					</tr>
					<%
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
					%>
					</table>
				</div>
			</div>
		</div>
		<div class="footer">
			<span>&copy福建省教育厅教学项目评估系统版权所有.&nbsp;&nbsp;转载内容版权归作者及来源网站所有</span>
		</div>
	</div>
	</body>
</html>
