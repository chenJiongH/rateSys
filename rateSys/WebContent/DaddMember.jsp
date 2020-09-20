<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head><meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<meta charset="utf-8">
		<title>添加专家组成员</title>
		<link href="css/addMember.css" rel="stylesheet"/>
		<script src="js/jquery-3.3.1.min.js"></script>
		<script src="js/noneRhead.js"></script>
		<script src="js/getWebSiteHead.js"></script>
		<script src="js/forbiddenFB.js"></script>
		<script src="js/forbiddenAuto.js"></script>
		<script src="js/getWelcomeName.js"></script>
		<style type="text/css">
	        table span{
	            display: inline-block;
	            width: 22px;
	            height: 22px;
				border-radius: 0.5rem;
				background-image: url(images/Uncheckbox.png);
	            vertical-align: middle;
	            border: #dedede solid 0px;
	        } 
	        input[type=radio]:checked+span{
         	   background-image: url(images/Chcheckbox.png);
	        }
	        input[type=radio]{
          	  display: none;
	        }
/* 	 	        配合重写框的样式    */
/* 		    $("table span").click(function(){  */
/*     			 $(this).prev().click(); */
/* 	    	 }) */
		</style>
		<script> 
			var curpage;
			var pageBean = {};
			var selectedData = {};
			var groups;//保存所有专家组 
			var allProject = {};
			var selectProName = "";
			var allProjectIdMap = {};// 保存可以作为输出的项目id，在输出时，只有专家组的pid在map内，才可以输出该专家组、专家组成员 
			var memberToProject = {};// 保存所有专家成员到项目名称的映射
			function messageDisplay() {
				$("#message").css("display", "inline-block");
			}
			function hid() {
				$("#message").css("display", "none");
			}
			
		 	$(function() {
		 		
				//固定表格头行 
				$(".tdiv").scroll(function() { //给table外面的div滚动事件绑定一个函数
					var top = $(".tdiv").scrollTop(); //获取滚动的距离
					//该行的所有单元格随着滚动 
					$("table tr:eq(0)").children().each(function() {
						$(this).css({
							"position": "relative",
							"top": top,
							"left": "0px",
	// 						"background-color": "white",
							"z-index": "21"
						});
					})
				})
		 		
				curpage = 1;
				$.post("AddMemberServlet/findPageBean", {'curpage':curpage}, function(data){
					if(data.message != undefined) {
						$("#message").text("页面请求失败，请刷新重试");
						messageDisplay();
						setTimeout(hid, 3000);
						return ;
					}
// 					修改专家组下拉框换文本框
					groups = data.group
// 					项目名称文本框 
					allProject = data.allProject;
					for(var i = 0;i < allProject.length; i++) 
						allProjectIdMap[allProject[i].pid] = true;
					pageBean.group = data.group;
					pageBean.people = data.people;
// 					if(data.member.length != 0)
					pageBean.member = data.member;
					dataShow(pageBean);
				}, 'json')
				
				$("#addData").click(function() {
					if($("#projectName").val() == "" || $("#groupName").val() == "" || $("#memberName").val() == "") {
						$("#message").text("请补充项目名称、专家组名称和专家组成员名称");
						messageDisplay(); 
						setTimeout(hid, 3000);
						return ;
					}
					// 获取当前的项目编号
					var pid = "";
					for(var i = 0; i < allProject.length; i++) {
						if(allProject[i].pname == $("#projectName").val())						
							pid = allProject[i].pid;
					}
					if(pid == "") {
						$("#message").text("请输入已存在的项目名称"); 
						messageDisplay();
						setTimeout(hid, 3000);
						return ;
					}
					//获取当前专家名称编号 
					var pspid = "";
					for(var i = 0; i < pageBean.people.length; i++) {
						if(pageBean.people[i].spname == $("#memberName").val())						
							pspid = pageBean.people[i].pspid;
					}
					if(pspid == "") {
						$("#message").text("请输入已存在的专家姓名");
						messageDisplay();
						setTimeout(hid, 3000);
						return ;
					}
					 
					//获取当前专家组的id 
					var pspgName = $("#groupName").val();
					var pspgid = "";
					for(var i = 0; i < pageBean.group.length; i++){
						if(pageBean.group[i].spgname == pspgName && pageBean.group[i].pid == pid) {
							pspgid = pageBean.group[i].pspgid;
							break;							
						}
					}
					if(pspgid == "") {
						$("#message").text("请输入当前项目下的专家组名称"); 
						messageDisplay();
						setTimeout(hid, 3000);
						return ;
					}
					//获取当前专家组的组长编号 
					var isleader = "";
					for(var i = 0; i < pageBean.member.length; i++) {
						if(pageBean.member[i].pspgid == pspgid)
							isleader = pageBean.member[i].isleader;
					}
					if(isleader == "") 
						isleader = pspid;
					$.post("AddMemberServlet/addData", {'pid' : pid,'pspid': pspid, 'pspgid': pspgid, 'isleader': isleader}, function(data) {
						if(data.message == "添加成功") 
							location.reload(true);
						else {
							$("#message").text(data.message);
							messageDisplay();
							setTimeout(hid, 3000);
						}
					}, 'json')
				})
				
				//点击查询按钮，获取符合查询条件的记录 
				$("#findData").click(function(){ 
					if($("#projectName").val() == "" && $("#groupName").val() == "" && $("#memberName").val() == "") {
						$("#message").text("请输入任意查询条件进行查询");
						messageDisplay();
						setTimeout(hid, 3000);
						return ;
					}
					var pspid = "";
					if($("#memberName").val() != "") {
						for(var i = 0; i < pageBean.people.length; i++) {
							if(pageBean.people[i].spname == $("#memberName").val())
								pspid = pageBean.people[i].pspid;
						}
						if(pspid == "") {
							$("#message").text("未查询到该专家信息");
							messageDisplay();
							setTimeout(hid, 3000);
							return ;							
						}
					}
					$.post("AddMemberServlet/queryData", {'pspid': pspid, 'pspgid': $("#groupName").val(), 'pname': $("#projectName").val() },function(data) {
						if(data.message != undefined) {
							$("#message").text(data.message);
							messageDisplay();
							setTimeout(hid, 3000);
							return ;
						}
						selectedData.group = data.group;
						selectedData.people = data.people;
						if(data.member.length != 0)
							selectedData.member = data.member;
						dataShow(selectedData);
					}, 'json')
				})
			}) 
			//点击分页事件
			function changePage(e) {
				pageBean = {};
				var content = $(e).text();
				if(content == "上一页") {
					if(curpage == 1)
						return;
					else 
						curpage--;
				} else if(content == "下一页") {
					if($("table").html() == "")
						return ;
					else 
						curpage++;
				} else curpage = parseInt(content);
				//如果此时有查询数据，则点击分页之后仍然显示此查询数据
				if(Object.keys(selectedData).length != 0){
					dataShow(selectedData);
					return ;
				}
				$.post("AddMemberServlet/findPageBean", {'curpage':curpage}, function(data){
					if(data.message != undefined) {
						$("#message").text("页面请求失败，请刷新重试");
						messageDisplay();
						setTimeout(hid, 3000);
						return ;
					}
// 					var options = "<option value='' selected='selected'></option>";
// 					for(var i = 0; i < data.group.length; i++) 
// 						options += "<option value='" + data.group[i].pspgid + "'>" + data.group[i].spgname + "</option>";		
// 					$("select").html(options);
					pageBean.group = data.group;
					pageBean.people = data.people;
// 					if(data.member.length != undefined && data.member.length != 0)
						pageBean.member = data.member;
					dataShow(pageBean);
				}, 'json')
			}			
		 	
			var proExist = {};// 用于输出项目名称，并且防止重复的输出
			//数据显示 
			function dataShow(data) {
				console.log(data);
		    	 var start = 1;
		    	 var end = 10;
		    	 //全局变量curpage，在点击分页和页面刷新时更新其值 
		    	 if(curpage >= 10) {
		    		 start = curpage - 8;
					 end = curpage + 1;				    		 
		    	 }
				$(".page").html("");
		    	 lis = "<li class='up' onclick='changePage(this);'>上一页</li>";
		    	 for(var i = start; i <= end; i++) {
		    		 if(i == curpage)
		    			 lis += "<li class='curpage' onclick='changePage(this);'>" + i + "</li>";
		    		 else lis += "<li onclick='changePage(this);'>" + i + "</li>";
		    	 }
		    	 lis += "<li class='next' onclick='changePage(this);'>下一页</li>";
		    	 $(".page").append(lis);
		    	 
		    	 $("table").html("");
		    	 trs = "<tr><th class='t1'>项目名称</th><th class='t0'>专家组名称</th><th class='t2'>专家组成员</th><th class='t4'>组长</th><th class='t3'>操作</th></tr>";
	    		  $("table").append(trs);
		    	 trs = "";
		    	 var tableJson = {};
		    	 tableJson.people = data.people;
		    	 tableJson.group = data.group;
		    	 tableJson.member = [];
		    	 if(Object.keys(selectedData).length != 0){
		    		 var mposition = (curpage - 1) * 20;
		    		 for(var count = 0; count < 20 && mposition < (selectedData.member).length ; count++,mposition++)
		    	 		tableJson.member.push((selectedData.member)[mposition]);
		    	 } else {
// 			    	 tableJson.member = data.member;
		    		 var mposition = (curpage - 1) * 20;
		    		 for(var count = 0; count < 20 && mposition < (data.member).length ; count++,mposition++)
		    	 		tableJson.member.push((data.member)[mposition]);
		    	 }
// 		    	 if(tableJson.member.length == 0) {
// 		    		 return ;
// 		    	 }
		    	 
		    	 for(var i = 0; i < tableJson.group.length; i++) {
		    		var flag = true;
		    		if(allProjectIdMap[tableJson.group[i].pid] != true)
		    			continue;
		    		for(var j = 0 ; j < tableJson.member.length; j++) {
		    			memberToProject[tableJson.member[j].pspid] = tableJson.group[i].pname;
		    			if(tableJson.member[j].pspgid == tableJson.group[i].pspgid) {
							trs = "<tr onclick='trClick(this);'>";
							// 先输出未输出的项目名称
			    			pname = tableJson.group[i].pname;
			    			if(proExist[pname] == undefined) {
			    				proExist[pname] = true;				
				    		 	trs += "<td class='t1'>" + pname + "</td>";
			    			}
			    		 	else 
			    		 		trs += "<td class='t1'></td>";
			    		 		
			    			if(flag)
					    		 trs += "<td class='t0'>" + tableJson.group[i].spgname + "</td>";
					    	else trs += "<td class='t0'></td>";
					    	var memberName = "";
					    	for(var k = 0; k < tableJson.people.length; k++) 
					    		if(tableJson.people[k].pspid == tableJson.member[j].pspid)
					    			memberName = tableJson.people[k].spname
				    		 trs += "<td class='t2'>" + memberName + "</td>";
				    		 if(tableJson.member[j].isleader == tableJson.member[j].pspid)
				    		 	trs += "<td class='t4'><input type='radio' value='" + tableJson.member[j].pspgid + "' name='" + tableJson.member[j].pspgid + "' checked='checked' onclick='changeLeader(this)'><span></span></td>";
				    		 else trs += "<td class='t4'><input type='radio' value='" + tableJson.member[j].pspgid + "' name='" + tableJson.member[j].pspgid + "' onclick='changeLeader(this)'><span></span></td>";
				    		 
				    		 trs += "<td class='t9'><input type='button' value='删除' onclick='delData(this)'></td>";
		
				    		 trs += "<td style='display:none' >" + tableJson.member[j].pspid + "</td>";
				    		 trs += "<td style='display:none' >" + tableJson.group[i].pspgid + "</td>";
				    		 trs += "<td style='display:none' >" + tableJson.group[i].spgname + "</td>";
				    		 trs += "<td style='display:none' >" + tableJson.group[i].pname + "</td>";
				    		 trs += "</tr>";
				    		 $("table").append(trs);
			    			 flag = false;
			    		}
		    		}
		    	 }
		    	 $("table span").click(function(){ //配合重写框的样式 
		    		 $(this).prev().click();
		    	 })
		    	 $("table tr:eq(1)").click();//点击第一行
// 		    	 console.log($("table tr:eq(1)"));
		    	 if($("table tr").length < 2)//第一行无记录 
		    		 $("#groupName").val(""),
		    		 $("#memberName").val("");
			}
		 	
		 	function changeLeader(e) {
		 		console.log($(e).parent().parent().find("td:eq(5)").text());
		 		$.post("AddMemberServlet/changeLeader", {'pspid': $(e).parent().parent().find("td:eq(5)").text(), 'pspgid': $(e).val()}, function(data) {})	
		 	}
		 	
			function delData(e) {
		      zdconfirm('系统确认框','确定从该专家组中删除该成员吗？',function(r){ //项目删除确定框 
		    	     if(r) {					
							$.post("AddMemberServlet/delData",{'pspid': $(e).parent().parent().find("td:eq(5)").text(), 'pspgid': $(e).parent().parent().find("td:eq(6)").text()}, function(data) {
								if(data.message == "删除成功")
									location.reload(true);
								else {
									$("#message").text(data.message);
									messageDisplay();
									setTimeout(hid, 3000);
								}
							}, 'json')
			    	      }
	    	    });  
			}
			
			function trClick(e) {
				var projectname = $(e).find("td:eq(8)").text();
				var membername = $(e).find("td:eq(2)").text();
				var groupname = $(e).find("td:eq(7)").text();
				$("#projectName").val(projectname);
				$("#memberName").val(membername);
				$("#groupName").val(groupname);
				console.log(membername + $("#memberName").val());
			}
		</script>
		<script>
//  		实现模糊查询功能 --> 修改 初始获取，然后中间遍历  
			var allMember;
			var flage = false;
			var flage2 = false;//标记专家组是否被点击 
			var flage3 = false;
			$(document).ready(function(){
				
				$.post("AddMemberServlet/findAllMember", function(data) {
					console.log(data);
					allMember = data;
				}, 'json')
				
				document.getElementById("memberName").oninput=function(){
					var val = $(this).val();
					if(val!=""){
						var lis = "";
						for(var i = 0; i < allMember.length; i++) {
							var flage = true; //拼音模糊查询 
							for(var j = 0; j < val.length; j++) {
								 if(allMember[i].pinyin.indexOf(val[j]) == -1) {
									 flage = false;//拼音匹配不成功
									 break;
								 }
							}
							if(allMember[i].spname.indexOf(val) != -1 || flage == true) //中文姓名模糊查询 
								lis += "<li onclick='mayNameClick(this)'>" + allMember[i].spname + " : " + allMember[i].sporganization + "</li>";
								
						}
						$("#mayName").css("display","block");
						$("#mayName").html(lis);
					} else{
							var lis = "";
							for(var i = 0; i < allMember.length; i++) {
								lis += "<li onclick='mayNameClick(this)'>" + allMember[i].spname + " : " + allMember[i].sporganization + "</li>";
						}
						$("#mayName").css("display","block");
						$("#mayName").html(lis);
					}
				}
				$("#memberName").click(function(){ //点击时，若为空，则显示所有专家 
					var val = $(this).val();
// 					if(val == "") { 点击就显示所有专家组成员 
							flage = true;
							var lis = "";
							for(var i = 0; i < allMember.length; i++) {
								lis += "<li onclick='mayNameClick(this)'>" + allMember[i].spname + " : " + allMember[i].sporganization + "</li>";
						}
						$("#mayName").css("display","block");
						$("#mayName").html(lis);
// 					}
				})
				
				$("body").click(function(){
					if(flage == false)
						$("#mayName").css("display","none");
					else 
						flage = false;
					if(flage2 == false)
						$("#mayGroup").css("display","none");
					else 
						flage2 = false;
					if(flage3 == false)						
						$("#mayProject").css("display","none");
					
					else 
						flage3 = false;
				})

				// 专家组文本框改动、则开始模糊查询
				document.getElementById("groupName").oninput=function(){
					var val = $(this).val();
					if(val!=""){
						var lis = "";
						console.log(groups);
						// 根据当前被选择的项目，显示对应的专家组 
						var pid = "";
						selectProName = $("#projectName").val();
						if(selectProName != "") {
							for(var i = 0; i < allProject.length; i++) 
								if(allProject[i].pname == selectProName) {
									pid = allProject[i].pid;
									break;
								}
							if(pid == "")
								return ;
						}
						console.log("pid" + ":" + pid);
						for(var i = 0; i < groups.length; i++) {
							if(groups[i].spgname.indexOf(val) != -1) {//中文姓名模糊查询
								if(groups[i].pid == pid || pid == "")
									lis += "<li onclick='mayGroupClick(this)'>" + groups[i].spgname + "</li>";
							} 
						} 
						$("#mayGroup").css("display","block"); 
						$("#mayGroup").html(lis);
					} else{
						var lis = "";
						for(var i = 0; i < groups.length; i++) {
							lis += "<li onclick='mayGroupClick(this)'>" + groups[i].spgname +"</li>";
						}
						$("#mayGroup").css("display","block");
						$("#mayGroup").html(lis);
					}
				}
				$("#groupName").click(function(){ //点击时，则显示所有专家 
					var val = $(this).val();
						flage2 = true;
						var lis = "";
						var pid = "";
						selectProName = $("#projectName").val();
						if(selectProName != "") {
							for(var i = 0; i < allProject.length; i++) 
								if(allProject[i].pname == selectProName) {
									pid = allProject[i].pid;
									break;
								}
							if(pid == "")
								return ;
						}
						console.log("pid" + ":" + pid);
						for(var i = 0; i < groups.length; i++) {
							if(groups[i].pid == pid || pid == "")
								lis += "<li onclick='mayGroupClick(this)'>" + groups[i].spgname +"</li>";
						}
						$("#mayGroup").css("display","block");
						$("#mayGroup").html(lis);
				})
				
				// 项目文本框改动、则在下面显示所有的项目 
				document.getElementById("projectName").oninput=function(){
					var val = $(this).val();
					if(val!=""){
						var lis = "";
						console.log(groups);
						for(var i = 0; i < allProject.length; i++) {
							if(allProject[i].pname.indexOf(val) != -1) //中文姓名模糊查询 
								lis += "<li onclick='mayProjectClick(this)'>" + allProject[i].pname + "</li>";
						} 
						$("#mayProject").css("display","block"); 
						$("#mayProject").html(lis);
					} else{
						var lis = "";
						for(var i = 0; i < allProject.length; i++) {
							lis += "<li onclick='mayProjectClick(this)'>" + allProject[i].pname +"</li>";
						}
						$("#mayProject").css("display","block");
						$("#mayProject").html(lis);
					}
				}
				
				$("#projectName").click(function(){ //点击时，则显示所有专家 
					var val = $(this).val();
						flage3 = true;
						var lis = "";
						for(var i = 0; i < allProject.length; i++) {
							lis += "<li onclick='mayProjectClick(this)'>" + allProject[i].pname +"</li>";
						}
						$("#mayProject").css("display","block");
						$("#mayProject").html(lis);
				})
			})
			 
			function mayNameClick(e) {  
				//专家组成员的值为当前点击值的文本去掉所在单位 
				
				$("#memberName").val($(e).text().substring(0, $(e).text().indexOf(' ')));
			}
			function mayGroupClick(e) {  
					
				//专家组成员的值为当前点击值的文本去掉所在单位 
				$("#groupName").val($(e).text());
			}

			function mayProjectClick(e) {  
				//项目的值为当前点击值的文本去掉所在单位 
				$("#projectName").val($(e).text());
				$("#groupName").val("");
				$("#mayGroup").html("");
			}
			// 当项目改变时，table显示该项目的所有专家组，专家组显示第一个专家组，成员显示该组第一个成员（可通过点击第一行，来实现），没有则显示第一个专家 (另行判断) 
			$(function() {
				$("#projectName").change(function(){
					$.post("AddMemberServlet/queryData", {'pspid': '', 'pspgid': '', 'pname': $("#projectName").val() },function(data) {
						console.log("start");
						console.log(data);
						if(data.message != undefined) {
							$("#message").text(data.message);
							messageDisplay();
							setTimeout(hid, 3000);
							return ;
						}
						selectedData.group = data.group;
						selectedData.people = data.people;
						if(data.member.length != 0)
							selectedData.member = data.member;
						dataShow(selectedData);
						// 当项目改变时，专家组显示第一个专家组，成员显示该组第一个成员（可通过点击第一行，来实现），没有则显示第一个专家 
						if(data.member.length == 0) {
							if(selectedData.people[0].spname != undefined)
								$("#memberName").val(selectedData.people[0].spname);
							if(selectedData.group[0].spgname != undefined)
								$("#groupName").val(selectedData.group[0].spgname);
						}
					}, 'json')
				})
			})
		</script>
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
				top: -20px;
				left: 60px;
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
		</style>
	</head>
	<body>
	<div class="wrapper" style="width:190.3rem; height:88rem; margin:0 auto;">
		<div class="header">
			<div class='headerContent'>
				<input onclick="window.location.href=('exit.jsp')" type="button" name="" id="" value="退出登录" />			
			</div>
		</div>
		<div class="main">
			<div class="location">当前位置：主页&nbsp;&nbsp;>&nbsp;&nbsp;添加专家组成员</div>
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
					<a href="DexInfoM.jsp"><li><img src="images/left3.png">专家信息</li></a> 
					<br>
					<a href="DexGroups.jsp"><li><img src="images/left6.png">建立专家组</li></a>
					<a href="GroupSchoolD.jsp"><li><img src="images/left7.png">专家组学校任务</li></a>
					<a href="DaddMember.jsp"><li class="now"><img src="images/left6-s.png">添加专家组成员</li></a>
					<a href="DexDis.jsp"><li><img src="images/left8.png">专家组任务分配</li></a>
					<br>
					<a href="Dproject_process_fallback.jsp"><li><img src="images/left9.png">项目流程回退</li></a>
					<a href="Dplace-on-file.jsp"><li><img src="images/left9.png">项目评分详情</li></a>
					<a href="DChangeSubPin.jsp"><li><img src="images/left2.png">下级人员密码修改</li></a>
					<a href="DchangePin.jsp"><li><img src="images/left9.png">修改个人密码</li></a>
				</ul>
			</div>
			<div class="right">
				<!-- <div class="rhead">
					添加专家组成员<span>ADD EXPERT GROUP MEMBERS</span>
				</div> -->
				<form action="">
				<label id='message' class='message'>提示信息</label>
					<span>项目名称：</span>
						<input type='text' id='projectName' name='projectName' autocomplete="off" style='background: #f8f8f8 url(images/selectArr.png) no-repeat 25.3rem center;padding-right: 20px;'>
						<ol id='mayProject' class='mayProjectOl'>
<!-- 							<li>123</li><li>123</li><li>123</li> -->
						</ol>
					<span style='margin-left: 1.5rem;    width: 17rem;'>专家组：</span>
						<input type='text' id='groupName' name='groupName' autocomplete="off" style='width: 171px;padding-right: 1.5rem;background: #f8f8f8 url(images/selectArr.png) no-repeat 15.2rem center;'> 
						<ol id='mayGroup' class='leftOl' style='    left: 48%;'>
<!-- 							<li>123</li><li>123</li><li>123</li> -->
						</ol>
					<span style='margin-left: 1.1rem;    width: 17rem;'>专家组成员：</span>
						<input type='text' id='memberName' name='memberName' autocomplete="off" style='width: 135px;padding-right: 1.5rem;background: #f8f8f8 url(images/selectArr.png) no-repeat 12.2rem center;'>
						<ol id='mayName' class='rightOl' style='left:80%'>
<!-- 							<li>123</li><li>123</li><li>123</li> -->
						</ol>
					<input style="display:none" type='text' id='pspid' name='pspid'>
					<input id='addData' type="button" value='添加'>
					<input id='findData' type="button" value='查询'>
				</form>
				<div class='tdiv'>
				<table>
					<thead>
						<tr>
							<th class='t1'>项目名称</th>
							<th class='t0'>专家组名称</th>
							<th class='t2'>专家组成员</th>
							<th class='t4'>组长</th>
							<th class='t3'>操作</th>
						</tr>
					</thead>
					<tbody>
<!-- 							<td class='t1'>fjut</td> -->
<!-- 							<td class='t2'>专家组</td> -->
<!-- 							<td class='t4'><input type='radio' name='' id=''></td> -->
<!-- 							<td class='t9'><input type="button" name="" id="" value="删除" onclick="delData(this)"></td> -->
					</tbody>
				</table>
				</div>
				<ul class="page"><li class="up" onclick="changePage(this);">上一页</li><li class="curpage" onclick="changePage(this);">1</li><li onclick="changePage(this);">2</li><li onclick="changePage(this);">3</li><li onclick="changePage(this);">4</li><li onclick="changePage(this);">5</li><li onclick="changePage(this);">6</li><li onclick="changePage(this);">7</li><li onclick="changePage(this);">8</li><li onclick="changePage(this);">9</li><li onclick="changePage(this);">10</li><li class="next" onclick="changePage(this);">下一页</li>
				</ul>
			</div>
		</div>
		<div class="footer">
			<span>&copy福建省教育厅教学项目评估系统版权所有.&nbsp;&nbsp;转载内容版权归作者及来源网站所有</span>
		</div>
	</div>
	</body>
</html>
<script type="text/javascript"> 
(function($) {
    
    $.alerts = {       
        alert: function(title, message, callback) {
            if( title == null ) title = 'Alert';
            $.alerts._show(title, message, null, 'alert', function(result) {
                if( callback ) callback(result);
            });
        },
         
        confirm: function(title, message, callback) {
            if( title == null ) title = 'Confirm';
            $.alerts._show(title, message, null, 'confirm', function(result) {
                if( callback ) callback(result);
            });
        },
             
        
        _show: function(title, msg, value, type, callback) {
          
                    var _html = "";
 
                    _html += '<div id="mb_box"></div><div id="mb_con"><span id="mb_tit">' + title + '</span>';
                    _html += '<div id="mb_msg">' + msg + '</div><div id="mb_btnbox">';
                      if (type == "alert") {
                      _html += '<input id="mb_btn_ok" type="button" value="确定" />';
                    }
                    if (type == "confirm") {
                      _html += '<input id="mb_btn_ok" type="button" value="确定" />';
                      _html += '<input id="mb_btn_no" type="button" value="取消" />';
                    }
                    _html += '</div></div>';
                 
                    //必须先将_html添加到body，再设置Css样式
                    $("body").append(_html); GenerateCss();
         
            switch( type ) {
                case 'alert':
        
                    $("#mb_btn_ok").click( function() {
                        $.alerts._hide();
                        callback(true);
                    });
                    $("#mb_btn_ok").focus().keypress( function(e) {
                        if( e.keyCode == 13 || e.keyCode == 27 ) $("#mb_btn_ok").trigger('click');
                    });
                break;
                case 'confirm':
                   
                    $("#mb_btn_ok").click( function() {
                        $.alerts._hide();
                        if( callback ) callback(true);
                    });
                    $("#mb_btn_no").click( function() {
                        $.alerts._hide();
                        if( callback ) callback(false);
                    });
                    $("#mb_btn_no").focus();
                    $("#mb_btn_ok, #mb_btn_no").keypress( function(e) {
                        if( e.keyCode == 13 ) $("#mb_btn_ok").trigger('click');
                        if( e.keyCode == 27 ) $("#mb_btn_no").trigger('click');
                    });
                break;
              
               
            }
        },
        _hide: function() {
             $("#mb_box,#mb_con").remove();
        }
    }
    // Shortuct functions
    zdalert = function(title, message, callback) {
        $.alerts.alert(title, message, callback);
    }
     
    zdconfirm = function(title, message, callback) {
        $.alerts.confirm(title, message, callback);
    };
         
 
    
    
    //生成Css
    var GenerateCss = function () {
    	 
        $("#mb_box").css({ width: '100%', height: '100%', zIndex: '99999', position: 'fixed',
          filter: 'Alpha(opacity=60)', top: '0', left: '0', opacity: '0.6'
        });
     
        $("#mb_con").css({ zIndex: '999999', width: '20%', position: 'fixed',
          backgroundColor: 'White', borderRadius: '1.5rem', boxShadow: '0px 0.3rem 0.9rem #8b8585'
        });
        
        $("#mb_tit").css({ display: 'block', fontSize: '1.5rem', color: '#455fe7', padding: '1rem 1.5rem 1.0rem 14.4rem', //系统确认框字眼控制
          backgroundColor: '#DDD', borderRadius: '1.5rem 1.5rem 0 0',
          borderBottom: '0.3rem solid #455fe7', fontWeight: 'bold'
        });
        $("#mb_btn_no").css({    'border-radius': '7.1rem',border: '0.1rem rgb(218, 220, 224) solid', backgroundColor: '#dddddd', color: '#455fe7', marginLeft: '2rem' });
        $("#mb_btn_ok").css({ backgroundColor: '#455fe7', borderRadius: '2.2rem', color: 'white' });
     
        $("#mb_msg").css({ padding: '2rem', lineHeight: '2rem', 'padding-left': '10rem',
          borderBottom: '0.1rem dashed #DDD', fontSize: '1.3rem'
        });
     
        $("#mb_ico").css({ display: 'block', position: 'absolute', right: '1rem', top: '0.9rem',
          border: '0.1 solid Gray', width: '1.8rem', height: '1.8rem', textAlign: 'center',
          lineHeight: '1.6rem', cursor: 'pointer', borderRadius: '1.2rem', fontFamily: '微软雅黑'
        });
     
        $("#mb_btnbox").css({ margin: '1.5rem 0 1rem 0', textAlign: 'center' });
        $("#mb_btn_ok,#mb_btn_no").css({ width: '8.5rem', height: '3rem', border: 'none' });
     
     
        //右上角关闭按钮hover样式
        $("#mb_ico").hover(function () {
          $(this).css({ backgroundColor: 'Red', color: 'White' });
        }, function () {
          $(this).css({ backgroundColor: '#DDD', color: 'black' });
        });
     
        var _widht = document.documentElement.clientWidth; //屏幕宽
        var _height = document.documentElement.clientHeight; //屏幕高
     
        var boxWidth = $("#mb_con").width();
        var boxHeight = $("#mb_con").height();
     
        //让提示框居中
        $("#mb_con").css({ top: (_height - boxHeight)/20 + "rem", left: (_widht - boxWidth)/20 + "rem" });
      }
     
     
    })(jQuery);
    	</script>
    		<script>
    		var oTitle = null;
    		var sTitle = null;
    	function titleShow() {
    		var aA = document.getElementsByTagName('td');
    		for (var i = 0; i < aA.length; i++) {
    			if (aA[i].title) { //假如a标签中存在title的话
    				createTitleDiv(aA[i])
    			}
    		}
    	}
    	function createTitleDiv(elementTitle) {
    		elementTitle.onmouseover = function(ev) {
    			sTitle = this.title;
    			this.title = '';
    			oTitle = document.createElement('div');
    			oTitle.className = 'titleShow';
    			oTitle.innerHTML = sTitle;
    			document.body.appendChild(oTitle);
    			 //获取鼠标所在x座标并增加10个像素,下同
    			oTitle.style.left = (ev.pageX + 10)/10 + 'rem';
    			oTitle.style.top = (ev.pageY + 10)/10 + 'rem';
    		};
    		elementTitle.onmousemove = function(ev) {
    			var ev = ev || window.event;
    			oTitle.style.left = (ev.pageX + 10)/10 + 'rem'; //获取鼠标所在x座标并增加10个像素,下同
    			oTitle.style.top = (ev.pageY + 10)/10 + 'rem';
    			$(oTitle).css("zIndex", "5555");
    		}
    		elementTitle.onmouseout = function() {
    			this.title = sTitle;
    			if(oTitle != null)
    				document.body.removeChild(oTitle);
    		}
    	}
    </script>