<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head><meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<meta charset="utf-8">
		<title>专家组任务分配</title>
		<link href="css/exDis.css" rel="stylesheet"/>
		<script src="js/jquery-3.3.1.min.js"></script>
		<script src="js/noneRhead.js"></script>
		<script src="js/getWebSiteHead.js"></script>
		<script src="js/forbiddenFB.js"></script>
		<script src="js/forbiddenAuto.js"></script>
		<script src="js/getWelcomeName.js"></script>
		<style>
			.titleShow {
				z-index: "555555";  
				max-width: 45rem;
				position: absolute;
				font: 1rem "微软雅黑" bold;
				color: black;
				border-radius: 0.5rem;
				border: #dedede solid 0.1rem;
				padding: 0.7rem;
				background: white;
				word-break: break-word;
			}
	        table span{
	            display: inline-block;
	            width: 22px;
	            height: 22px;
				border-radius: 0.5rem;
				background-image: url(images/Uncheckbox.png);
	            vertical-align: middle;
    			position: absolute;
   			    left: 0.5rem;
   			    cursor: pointer;
	            border: #dedede solid 0px;
	        } 
	        /*
 	        input[type=checkbox]:checked+span { 
          	   background-image: url(images/Chcheckbox.png); 
 	        }
 	        */
	        input[type=checkbox] {
          	  display: none;
	        }
		</style>

		<script>
			$(function() {
				$(".indexs").scroll(function() { //给table外面的div滚动事件绑定一个函数
					var top = $(".indexs").scrollTop(); //获取滚动的距离
					//该行的所有单元格随着滚动 
					$("table tr:eq(0)").children().each(function() {
						$(this).css({
							"position": "relative",
							"top": top,
							"left": "0px",
//	 						"background-color": "white",
							"z-index": "1"
						}); 
					})
				})
				// 初始化显示页面数据
				$.post("ExDisServlet/findAllPr", function(data){
					console.log(data);
					if(data.message != undefined) {
						return ;
					}
					var options = "";
					for(var i = 0; i < data.length; i++) 
						options += "<option value='" + data[i].pid + "'>" + data[i].Pname + "</option>";
					$("#project").html(options);
					// 联动查找专家组和组成员 
					$("#project").change();
				}, 'json')
				
				$("#project").change(function() {
					$.post("ExDisServlet/findGroup", {'pid' : $("#project").val()}, function(data) {
						//在下拉列表中显示项目下的专家组
						var options = "";
						for(var i = 0; i < data.length; i++)
							options += "<option value='" + data[i].gid + "'>" + data[i].gname + "</option>";
						$("#group").html(options);
						$("#group").change();
					}, 'json')
				})
				//专家组改变 
				$("#group").change(function() {
					$.post("ExDisServlet/findMember", {'gid': $("#group").val()}, function(data) {
						console.log(data);
						//在下拉列表中显示专家组成员 
						var options = "";
						for(var i = 0; i < data.length; i++)
							options += "<option value='" + data[i].spid + "'>" + data[i].spname + "</option>";
						$("#member").html(options);
						//根据当前专家组 和 专家组成员 查找所有的指标和已存在的成员指标信息
						$("#member").change();
					}, 'json')
				})
				
				//组成员改变
				$("#member").change(function() {
					$.post("ExDisServlet/findIndex", {'gid': $("#group").val(), 'spid': $("#member").val()}, function(data) {
						console.log(data);
						memberIndexShow(data);
					}, 'json')
				})
			})
			//显示table数据 
			//构造abChildrenNum集合，里面以AB指标编号为键，以其下级C指标编号个数为值 
			var abChildrenNum = {};
			//构造usedChildrenNum集合，里面以AB指标编号为键，以其已被分配的下级C指标个数为值
			var usedChildrenNum = {};
			var remainC = 0;
			function memberIndexShow(data) {
				remainC = 0
				//以空间换时间，优化i循环内部的判断 
				var memberC = {};
				for(var j = 0; j < data.mi.length; j++) 
					memberC[data.mi[j].cid] = true;
				console.log(memberC);
// 				<td class='t1'>A级指标名<input type='checkbox' name='' ></td>
// 				<td class='t2'>B级指标名<input type='checkbox' name='' ></td>
// 				<td class='t3'>C级指标名<input type='checkbox' name='' ></td>
				var aname = "",
					bname = "",
					aid = "",
					bid = "";
				//id属性为自己编号 ，name属性为父亲编号
				trs = "<tr ><th class='t1'>A级指标</th><th class='t2'>B级指标</th><th class='t3'>C级指标</th></tr>";
				for(var i = 0; i < data.index.length; i++) {
					if(data.index[i].spid != null && data.index[i].spid != $("#member").val()) //其他专家成员的指标，不显示 
						continue ;
					trs += "<tr>";
					if(data.index[i].aname == aname) 
						trs += "<td class='t1'></td>";
					else 
						aname = data.index[i].aname,
						aid = data.index[i].aid,
						abChildrenNum[aid] = 0,
						usedChildrenNum[aid] = 0,
						trs += "<td class='t1' title='" + aname + "'>" + aname + "<input type='checkbox' name='' id='" + aid +"'><span></span></td>";
						
					if(data.index[i].bname == bname) trs += "<td class='t2'></td>";
					else 
						bname = data.index[i].bname,
						bid = data.index[i].bid,
						abChildrenNum[bid]=0,
						usedChildrenNum[bid] = 0,
						trs += "<td class='t2' title='" + bname + "'>" + bname + "<input type='checkbox' name='" + aid + "' id='" + bid + "'><span></span></td>";
						
					if(memberC[data.index[i].cid] != undefined)
						usedChildrenNum[aid]++, 
						usedChildrenNum[bid]++,
						trs += "<td class='t3' title='" + data.index[i].cname + "'>" + data.index[i].cname + "<input type='checkbox' name='" + bid + "' checked='checked' id='" + data.index[i].cid + "'><span></span></td>";
					else 
						trs += "<td class='t3' title='" + data.index[i].cname + "'>" + data.index[i].cname + "<input type='checkbox' name='" + bid + "' id='" + data.index[i].cid + "'><span></span></td>", remainC++;
					abChildrenNum[bid]++,
					abChildrenNum[aid]++,
					trs += "</tr>";
				}
				$("table").html("");
				$("table").append(trs);
				titleShow();
		    	 $("table span").click(function(){ //配合重写框的样式 
		    		 $(this).prev().click();
		    	 })
				$("table tr input[id^=C]").each(function() {
					if($(this).prop("checked") == true) {
						$(this).next().css("backgroundImage", "url(images/Chcheckbox.png)");
					}
				})
		    	 
				$(".remainC").text(remainC);
				console.log(abChildrenNum);
				//初始化 ： 根据C指标设置其父级AB指标复选框的状态
				for(key in abChildrenNum) {
					if(usedChildrenNum[key] != 0) {
						if(abChildrenNum[key] != usedChildrenNum[key]) {
							$("#" + key).prop("indeterminate", true);
							$("#" + key).prop("checked", true);
							$("#" + key).next().css("backgroundImage", "url(images/uncertain.png)");
						}
						else {
							$("#" + key).prop("indeterminate", false);
							$("#" + key).prop("checked", true);
							$("#" + key).next().css("backgroundImage", "url(images/Chcheckbox.png)");					
						}
					}
				}
				//改变剩余C指标数量 
				$("input[id^='C']").click(function() {
					var state = $(this).prop("checked");
					//勾选C级指标，减少剩余指标数，并显示。反之增加剩余量 
					$(".remainC").text( state?--remainC:++remainC );
					//获取其上级AB指标 
					var bid = $(this).prop("name"),
						aid = $("#" + bid).prop("name");
					if(state == true) {
						$(this).next().css("backgroundImage", "url(images/Chcheckbox.png)");
// 						发送异步增加记录请求 
						$.post("ExDisServlet/addMemIndex", {'gid': $("#group").val(), 'spid': $("#member").val(), 'cid': $(this).prop("id")}, function(){})
						
						usedChildrenNum[bid]++;
						usedChildrenNum[aid]++;
						//改变其父级b指标状态 
						if(usedChildrenNum[bid] == abChildrenNum[bid]) {
							$("#" + bid).prop("indeterminate", false);
							$("#" + bid).prop("checked", true);
							$("#" + bid).next().css("backgroundImage", "url(images/Chcheckbox.png)");
						} else {
							$("#" + bid).prop("indeterminate", true);
							$("#" + bid).prop("checked", true);
							$("#" + bid).next().css("backgroundImage", "url(images/uncertain.png)");
						}
						//改变其父级a指标状态 
						if(usedChildrenNum[aid] == abChildrenNum[aid]) {
							$("#" + aid).prop("indeterminate", false);
							$("#" + aid).prop("checked", true);
							$("#" + aid).next().css("backgroundImage", "url(images/Chcheckbox.png)");
						} else {
							$("#" + aid).prop("indeterminate", true);
							$("#" + aid).prop("checked", true);
							$("#" + aid).next().css("backgroundImage", "url(images/uncertain.png)");
						}
					} else {
						$(this).next().css("backgroundImage", "url(images/Uncheckbox.png)");
						//发送异步删除 记录请求 
						$.post("ExDisServlet/delMemIndex", {'gid': $("#group").val(), 'spid': $("#member").val(), 'cid': $(this).prop("id")}, function(){})
						
						usedChildrenNum[bid]--;
						usedChildrenNum[aid]--;
						if(usedChildrenNum[bid] == 0) {
							$("#" + bid).prop("indeterminate", false);
							$("#" + bid).prop("checked", false);
							$("#" + bid).next().css("backgroundImage", "url(images/Uncheckbox.png)");
						} else {
							$("#" + bid).prop("indeterminate", true);
							$("#" + bid).prop("checked", true);
							$("#" + bid).next().css("backgroundImage", "url(images/uncertain.png)");
						}
						if(usedChildrenNum[aid] == 0) {
							$("#" + aid).prop("indeterminate", false);
							$("#" + aid).prop("checked", false);
							$("#" + aid).next().css("backgroundImage", "url(images/Uncheckbox.png)");
						} else {
							$("#" + aid).prop("indeterminate", true);
							$("#" + aid).prop("checked", true);
							$("#" + aid).next().css("backgroundImage", "url(images/uncertain.png)");
						}
					}
				})
// 				点击B指标，调用下面的C.click
				$("input[id^='B']").click(function() {
					var state = $(this).prop("checked"),
						aid = $(this).prop("name"),
						cid = $(this).prop("id");
					$(this).prop("indeterminate", false);
					if(state == false) {
						$("input[name='" + cid +"'").each(function() {
							if($(this).prop("checked")) {
								$(this).click();
							}
						})
					} else {
						$("input[name='" + cid +"'").each(function() {
							if(!$(this).prop("checked")) {
								$(this).click();
							}
						})
					}
				})
				//点击A递归调用下面的B.click 
				$("input[id^='A']").click(function() {
					var state = $(this).prop("checked"),
						bid = $(this).prop("id");
					$(this).prop("indeterminate", false);
					if(state == false) {
						$("input[name='" + bid +"'").each(function() {
							if($(this).prop("checked")) {
								$(this).click();
							}
						})
					} else {
						$("input[name='" + bid +"'").each(function() {
							if(!$(this).prop("checked")) {
								$(this).click();
							}
						})
					}
				})
			}
		</script>
	</head>
	<body>
	<div class="wrapper">
		<div class="header">
			<div class='headerContent'>
				<input onclick="window.location.href=('exit.jsp')" type="button" name="" id="" value="退出登录" />			
			</div>
		</div>
		<div class="main">
			<div class="location">当前位置：主页&nbsp;&nbsp;>&nbsp;&nbsp;专家组任务分配</div>
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
					<li style="height: 70px;margin-top: 20px;">
					<span class="s1" style='margin-left: -1rem;display: inline-block;'>欢迎您！</span>
					<span class="s2"><span class='welcomeName' style='display: flex;width: 120px;'></span></span>
					</li>
					<a href="CSet.jsp"><li><img src="images/left1.png">区划和学校</li></a>
					<a href="acM.jsp"><li><img src="images/left1.png">下级账号</li></a>
<!-- 					<a href="changeSubPin.jsp"><li><img src="images/left2.png">下级密码</li></a> -->
					<li style='padding: 0px;height: 1.2rem;'></li>
					
					<a href="pjInfoM.jsp"><li><img src="images/left4.png">评估项目</li></a>
					<a href="indexSet.jsp"><li><img src="images/left5.png">评估指标</li></a>
					<li style='padding: 0px;height: 1.2rem;'></li>
					
					<a href="exInfoM.jsp"><li><img src="images/left3.png">专家信息</li></a>
					<a href="taskM.jsp"><li><img src="images/left7.png">项目学校任务</li></a>
					<a href="exGroups.jsp"><li><img src="images/left6.png">新建专家组</li></a>
					<a href="GroupSchoolP.jsp"><li><img src="images/left7.png">专家组学校任务</li></a>
					<a href="addMember.jsp"><li><img src="images/left6.png">添加专家组成员</li></a>
					<a href="exDis.jsp"><li class="now"><img src="images/left8-s.png">专家组任务分配</li></a>
					<li style='padding: 0px;height: 1.2rem;'></li>
					
					<a href="project_process_fallback.jsp"><li><img src="images/left9.png">项目流程回退</li></a>
					<a href="place-on-file.jsp"><li><img src="images/left9.png">项目评分详情</li></a>
					<a href="changePin.jsp"><li><img src="images/left9.png">个人密码修改</li></a>
				</ul>
			</div>
			<div class="right">
				<div class="rhead">
					专家组任务分配<span>TASK ASSIGNMENT OF EXPERT GROUP</span>
				</div>
				<form action="">
					项目：<select id='project' style='width:260px'></select>
					<span>专家组：</span><select id="group"></select>
					<span>组内专家：</span><select id="member"></select>
					<span style='position: absolute;top: -9rem;right: 2.3rem;'>剩余C指标数：<span class='remainC'></span></span>
					<div class="indexs">
						<table class="table">
							<tr style='background-color: #eceffd;color: #455fe7;'>
								<th class='t1'>A级指标</th>
								<th class='t2'>B级指标</th>
								<th class='t3'>C级指标</th>
							</tr>
							<tr>
<!-- 								<td class='t1'>A级指标名<input type='checkbox' name='' ></td> -->
<!-- 								<td class='t2'>B级指标名<input type='checkbox' name='' ></td> -->
<!-- 								<td class='t3'>C级指标名<input type='checkbox' name='' ></td> -->
							</tr>
						</table>
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
<script>
function titleShow() {
	var oTitle = null;
	var sTitle = null;
	var aA = document.getElementsByTagName('td');
	for (var i = 0; i < aA.length; i++) {
		if (aA[i].title) { //假如td标签中存在title的话
			aA[i].onmouseover = function(ev) {
				sTitle = this.title;
				this.title = '';
				oTitle = document.createElement('div');
				oTitle.className = 'titleShow';
				oTitle.innerHTML = sTitle;
				document.body.appendChild(oTitle);
			};
			aA[i].onmousemove = function(ev) {
				var ev = ev || window.event;
				oTitle.style.left = (ev.pageX)/10 + 'rem'; //获取鼠标所在x座标并增加10个像素,下同
				oTitle.style.top = (ev.pageY + 10) + 'rem';
				$(oTitle).css("zIndex", "5555");
			}
			aA[i].onmouseout = function() {
				this.title = sTitle;
				document.body.removeChild(oTitle);
			}
		}
	}
}
</script>