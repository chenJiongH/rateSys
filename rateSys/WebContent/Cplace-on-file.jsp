<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title>项目评分详情</title>
		<link href="css/PSPRate.css" rel="stylesheet"/>
		<link href="css/leftLi.css" rel="stylesheet"/>
		<style type="text/css">
	    </style>
		<style>
			.titleShow {
				max-width: 45rem;
				position: absolute;
				font: 1.5rem "微软雅黑"bold;
				color: black;
				border-radius: 0.5rem;
				border: #dedede solid 0.1rem;
				padding: 0.7rem;
				background: white;
				word-break: break-word;
				z-index: "555555";
			}	
		</style>
		<script src="js/jquery-3.3.1.min.js"></script>
		<script src="js/getWebSiteHead.js"></script>
		
<!-- 		富文本编辑器 -->
		<script type="text/javascript" charset="utf-8" src="utf8-jsp/ueditor.config.js"></script>
 	 	<script type="text/javascript" charset="utf-8" src="utf8-jsp/ueditor.all.js"> </script>
		<script type="text/javascript" charset="utf-8" src="utf8-jsp/lang/zh-cn/zh-cn.js"></script>
		
		<script src="js/forbiddenFB.js"></script>
		<script src="js/forbiddenAuto.js"></script>
		<script src="js/getWelcomeName.js"></script>
		<script>
		var curpage = "";
		var prJson = {}; // 存放项目名称到项目id的映射。归档项目的id为本身名称 
		var schJson = {};// 存放学校名称到学校id的映射。归档项目的学校id为本身名称
		//存放所有分数和说明的json数组，用于提交和保存 
		var isOnSpot = 0; // 是否抽查表示 
		var projects = {}; // 存放所有项目 
		var formData = {};
		var readonly = false;
		var nowLeft = "0px";// 当前div的水平滚动距离 
		var HTML_FONT_SIZE = 10;
		// 该数组用于导出数据 
		var exportList = [];
		var exportElem = {};
		function messageDisplay() {
			$("#message").css("display", "inline-block");
		}
		function hid() { 
			$("#message").css("display", "none");
		}
			$(function() {
				// 搭配单选框样式重写 
				$(".lab").click(function(){
					$(this).prev().click();
				})
				// 获取屏幕分辨率 
				var HtmlWidth = screen.width;
				if(HtmlWidth ==1366 || HtmlWidth ==1360)
					HTML_FONT_SIZE = 9.5; 
				if(HtmlWidth ==1280)
					HTML_FONT_SIZE = 9;
				if(HtmlWidth == 800)
					HTML_FONT_SIZE = 6;
				if(HtmlWidth == 400)
					HTML_FONT_SIZE = 4; 
				$(".tdiv").scroll(function() { //给table外面的div滚动事件绑定一个函数
					var top = $(".tdiv").scrollTop(); //获取滚动的距离
						//该行的所有单元格随着滚动 
					$("#row1").children().each(function() {
						$(this).css({
							"position": "relative",
							"top": top,
						});
					})
					$("#row2").children().each(function() {
						$(this).css({
							"position": "relative",
							"top": top,
						});
					})
					var bottomBarTop = (top + 598) + "px";
				 	$(".bottom-bar").css({
							"top": bottomBarTop
					});
				})
				// 初始显示所有的在评项目 
				$("#doing").click();
				$(":radio").change();
			    //查找页面待显示内容，即当前校级管理员指标内容
				$.post("PlaceOnFileServlet/findPr", {'prType':$(":radio:checked").val()}, function(data) {
					// 调用项目种类单选框改变函数
					prTypeChange(data);
					//请求当前所在项目的所有学校
					$.post("PlaceOnFileServlet/findSchoolByPid", {'pid' : prJson[$("#prInput").val()]}, function(data) {
						// 调用项目改变函数 
						prChange(data);
						//请求当前所在项目的所有学校和专家C指标信息 
						$.post("PlaceOnFileServlet/findCByPidSid", {'pid' : prJson[$("#prInput").val()], 'sid' : schJson[$("#schoolInput").val()]}, function(data) {
							// 调用学校改变函数
							schChange(data);
						}, 'json')
					}, 'json')
				}, 'json')
				
				$(":radio").change(function() {
				    //查找页面待显示内容，即当前校级管理员指标内容
					$.post("PlaceOnFileServlet/findPr", {'prType':$(":radio:checked").val()}, function(data) {
						// 调用项目种类单选框改变函数
						prTypeChange(data);
						//请求当前所在项目的所有学校
						$.post("PlaceOnFileServlet/findSchoolByPid", {'pid' : prJson[$("#prInput").val()]}, function(data) {
							// 调用项目改变函数 
							prChange(data);
							//请求当前所在项目的所有学校和专家C指标信息 
							$.post("PlaceOnFileServlet/findCByPidSid", {'pid' : prJson[$("#prInput").val()], 'sid' : schJson[$("#schoolInput").val()]}, function(data) {
								// 调用学校改变函数
								schChange(data);
							}, 'json')
						}, 'json')
					}, 'json')
				})
				//项目下拉框改变，重新请求相应项目的指标 
				$("#prInput").change(function() {
					// 当前输入不合法 
					if(prJson[$("#prInput").val()] == undefined) {
						$("#school").html("");
						$("#schoolInput").val("");
						exportList = [];
						// 分页去除上一页的行数据
						 $("table tr").each(function() {
							 console.log(this);
							 if($(this).prop("id") != "row1" && $(this).prop("id") != "row2") {
								 $(this).remove();
							 }
						 })
						 ue.setContent("");
						return;
					}
					//请求当前所在项目的所有学校 
					$.post("PlaceOnFileServlet/findSchoolByPid", {'pid' : prJson[$("#prInput").val()] }, function(data) {
						prChange(data);
						//请求当前所在项目的所有学校和专家C指标信息 
						$.post("PlaceOnFileServlet/findCByPidSid", {'pid' : prJson[$("#prInput").val()], 'sid' : schJson[$("#schoolInput").val()]}, function(data) {
							schChange(data);
						}, 'json')
						
					}, 'json')
				})
				
				$("#schoolInput").change(function() {
					// 当前输入不合法 
					if(schJson[$("#schoolInput").val()] == undefined) {
						exportList = [];
						// 分页去除上一页的行数据
						 $("table tr").each(function() {
							 console.log(this);
							 if($(this).prop("id") != "row1" && $(this).prop("id") != "row2") {
								 $(this).remove();
							 }
						 })
						 ue.setContent("");
						return;
					}
					//请求当前所在项目的所有学校和专家C指标信息 
					$.post("PlaceOnFileServlet/findCByPidSid", {'pid' : prJson[$("#prInput").val()], 'sid' : schJson[$("#schoolInput").val()]}, function(data) {
						schChange(data);
					}, 'json')
				})
			})
			
			function prTypeChange(data) {
				console.log(data); 
				if(data.message != undefined) {
					$("#message").html("<img src='images/warn.png' class='messageWarn'>" + data.message);
					messageDisplay();
					setTimeout(hid, 3000);
		    		return ;
				}
				//显示所有已开启并所有学校完成自评的项目
				projects = data;
				var options = "";
				// 这一句是为了让初始时，文本框有值 
				$("#prInput").val(data[0].pname);
				// 下拉框显示所有项目
				for(var i = 0; i < data.length; i++) {
					if(data[i].pid != undefined	){
						options += "<option value='" + data[i].pname + "' ></option>";
						// 根据选中的pname 找到pid，再传给后台pid，进行查询。 
						prJson[data[i].pname] = data[i].pid;						
					}					
					else {
						options += "<option value='" + data[i].pname + "' ></option>";
						prJson[data[i].pname] = data[i].pname;						
					}
				}
				$("#Pr").html(options);
			}
			
			function prChange(data) {
				console.log(data);
				var options = "";
				// 这一句是为了让初始时，文本框有值 
				$("#schoolInput").val(data[0].sname);
				// 归档项目的学校没有id属性，id就是姓名。在评项目则有id属性
				for(var i = 0; i < data.length; i++) {
					if(data[i].sid != undefined	) {
						options += "<option value='" + data[i].sname + "' ></option>";
						schJson[data[i].sname] = data[i].sid;						
					}	
					else {
						options += "<option value='" + data[i].sname + "' ></option>";
						schJson[data[i].sname] = data[i].sname;	
					}
				}
				$("#school").html(options);
			}
			
			function schChange(data) {
				exportList = [];
				// 分页去除上一页的行数据
				 $("table tr").each(function() {
					 console.log(this);
					 if($(this).prop("id") != "row1" && $(this).prop("id") != "row2") {
						 $(this).remove();
					 }
				 })
				console.log(data);
				if(data.message != undefined) {
					$("#message").html("<img src='images/warn.png' class='messageWarn'>" + data.message);
					messageDisplay();
					setTimeout(hid, 3000);
					return ;
				}
				dataShow(data);
				// 显示总评图片
				setTimeout(function() {
				ue.setContent(data[0].reportLocation);
				}, 500);
			}
			
			// 该项目已经被提交，设置全屏只读 
			function setReadonly() {
				$("#draft").removeAttr("onclick");
				$(".in2").removeAttr("onclick");
				$("#prInput").removeAttr("readonly");
				$("#schoolInput").removeAttr("readonly");
				readonly = true;
			}
			//显示表格数据
			function dataShow(data) {
				$(".titleShow").remove();
				//开始标志，减少循环次数 ,0 未遇到该页，1正在显示该页，2该页显示完毕 
				var start = 0;
				var aname = "";
				var bname = "";
				var trs = "";
				// 统计评分
				var scoreType = [0, 0, 0, 0, 0];
				// 标记评分，查看是否有未评分的级别，0表示未评分，1表示已经评分 
				var scoreFlag = [0, 0, 0, 0, 0];
				console.log(scoreType);
				for(var i = 0; i < data.length ; i ++) {
					start = 1;
					trs += "<tr>";
					if(aname != data[i].aname)
						aname = data[i].aname,
						trs += "<td class='t1'  style='left: " + nowLeft + ";'   title='"+data[i].aname+"'>" + data[i].aname + "</td>";
					else trs += "<td class='t1' style='left: " + nowLeft + ";'  ></td>"; 
					if(bname != data[i].bname)
						bname = data[i].bname,
						trs += "<td class='t2'  style='left: " + nowLeft + ";'  title='" + data[i].bname+"'>" + data[i].bname + "</td>";
					else trs += "<td class='t2'  style='left: " + nowLeft + ";'  ></td>";
					trs += "<td class='t3' style='left: " + nowLeft + ";'   title='" +data[i].cname +"'>" + data[i].cname + "</td>";
					trs += "<td class='t4' style='left: " + nowLeft + ";'  >" + data[i].score + "</td>";
					if(data[i].schoolScore == null) data[i].schoolScore = "";
					else scoreFlag[0] = 1;
					trs += "<td class='t5' style='left: " + nowLeft + ";'  >" + data[i].schoolScore + "</td>";
					scoreType[0] += Number(data[i].schoolScore);
					trs += "<td class='t6' style='left: " + nowLeft + ";'  ><input type='button' value='查看' name='" + data[i].description + "' onclick='lookDescri(this)'></td>";
					if(data[i].annexLocation !== null && data[i].annexLocation !== "")
						trs += "<td class='t6' style='left: " + nowLeft + ";' ><input type='button' class='" + data[i].sid + "" + data[i].cid +"' value='下载' name='" + data[i].annexLocation + "' onclick='download(this)'></td>";
					else trs += "<td class='t6' style='left: " + nowLeft + ";' ></td>";
					if(data[i].distScore == null) data[i].distScore = "";
					else scoreFlag[1] = 1;
					if(data[i].distExplain == null) data[i].distExplain = "";
					if(data[i].dspName == null) data[i].dspName = "";
					scoreType[1] += Number(data[i].distScore);
					trs += "<td class='t7'><input type='text' readonly='readonly' value='" + data[i].distScore + "'></td>";
					trs += "<td class='t9'><input type='text' readonly='readonly' value='" + data[i].dspName + "'></td>";
					trs += "<td class='t8' title='"+data[i].distExplain+"'><input readonly='readonly' type='text' value='" + data[i].distExplain + "'></td>";

					if(data[i].cityScore == null) data[i].cityScore = "";
					else scoreFlag[2] = 1;
					if(data[i].cityExplain == null) data[i].cityExplain = "";
					if(data[i].cspName == null) data[i].cspName = "";
					scoreType[2] += Number(data[i].cityScore);
					trs += "<td class='t7'><input type='text' readonly='readonly' value='" + data[i].cityScore + "'></td>";
					trs += "<td class='t9'><input type='text' readonly='readonly' value='" + data[i].cspName + "'></td>";
					trs += "<td class='t8' title='"+data[i].cityExplain+"'><input readonly='readonly' type='text' value='" + data[i].cityExplain + "'></td>";
		
					
				}
				// 记录导出数据;
				exportList = data;
				// 统计评分
				console.log(scoreType);
				if(scoreFlag[0] == 0) scoreType[0] = "未评";
				else scoreType[0] = scoreType[0].toFixed(1);
				$("#schScore").text(scoreType[0] + "分");
				
				if(scoreFlag[1] == 0) scoreType[1] = "未评"; 
				else scoreType[1] = scoreType[1].toFixed(1);
				$("#distScore").text(scoreType[1] + "分");
				
				if(scoreFlag[2] == 0) scoreType[2] = "未评";
				else scoreType[2] = scoreType[2].toFixed(1);
				$("#cityScore").text(scoreType[2] + "分");
				
				if(scoreFlag[3] == 0) scoreType[3] = "未评";
				else scoreType[3] = scoreType[3].toFixed(1);
				$("#proScore").text(scoreType[3] + "分");
				
				if(scoreFlag[4] == 0) scoreType[4] = "未评";
				else scoreType[4] = scoreType[4].toFixed(1);
				$("#spotScore").text(scoreType[4] + "分");
				
				$("table").append(trs);
				titleShow();
				$("input").prop("autocomplete", "off");
				// 不要让.bottom-bar 覆盖到最后一行
				$("tr:last").css({"height": "4.8rem"});
				$("tr:last").children().each(function(){
					$(this).css({'position': 'relative', 'top': '-0.6rem'});
				})
			    	$("input").attr("readonly", "readonly");

				$("#prInput").removeAttr("readonly");
				$("#schoolInput").removeAttr("readonly");
				
			}
			//点击下载某个C指标附件或总评文件 
			function download(e) {
				if($(e).prop('name') == "") {return;}
				$("#form2 input").val($(e).prop('name'));
				$("#form2").submit();
				$(e).css("backgroundColor","rgb(150, 132, 224, 0.5)");
			}
			// 导出数据 
			function exportRate() {
				// 塞入项目名和校名
				var location = {};
				// location.project = $("#Pr").val();
				location.pname = prJson[$("#prInput").val()]; // 项目id 或 项目名
				location.bname = $("#prInput").val(); // 项目名 
				// location.school = $("#school").val();
				location.aname = schJson[$("#schoolInput").val()]; // 学校id 或 学校名
				location.cname = $("#schoolInput").val(); // 学校名
				
				exportList.push(location);
				console.log(exportList);
				$("#data").val(JSON.stringify(exportList));
				$("#exportXls").submit();
			}
			function exportScore() {
				// 塞入项目下的指标个数（用于多个学校在同一行，指标个数作为偏移量）
				var location = {};
				location.bname = $("#prInput").val(); // 项目名 
				
				var exportAllSchoolList = [];
				// 先请求所有该项目下所有的学校数据，然后再导出
				$("#school").children().each(function() {
					var sname = $(this).val();
					$.ajax({
						type:"post",
						url:"PlaceOnFileServlet/findCByPidSid",
						async:false,//设置同步/异步的参数[true异步  false同步]
						data:{'pid' : prJson[$("#prInput").val()], 'sid' : schJson[sname]},
						dataType:"json",
						success:function(data){
							location.aname = data.length; // 项目下的指标个数，（用于多个学校在同一行，指标个数作为偏移量）
							// 数组合并，将多个学校信息合在一起
							for(var i = 0; i < data.length; i++) {
								data[i].schoolName = sname;        // 当前学校姓名
								exportAllSchoolList.push(data[i]);
							}
						}
					});
				})
				exportAllSchoolList.push(location);
				console.log(exportAllSchoolList);
				$("#allScore").val(JSON.stringify(exportAllSchoolList));
				$("#exportScore").submit();
			}
		</script>
	</head>
	<body>
	<div class="wrapper" style="width:190.3rem; margin:0 auto;">
		<div class="header">
			<div class='headerContent'>
				<input onclick="window.location.href=('exit.jsp')" type="button" name="" id="" value="退出登录" />			
			</div>
		</div>
		<div class="main">
			<div class="location">当前位置：主页&nbsp;&nbsp;>&nbsp;&nbsp;专家评分</div>
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
					<a href="CexInfoM.jsp"><li><img src="images/left3.png">专家信息</li></a>
					<br>
					<a href="CexGroups.jsp"><li><img src="images/left6.png">建立专家组</li></a>
					<a href="GroupSchoolC.jsp"><li><img src="images/left7.png">专家组学校任务</li></a>
					<a href="CaddMember.jsp"><li><img src="images/left6.png">添加专家组成员</li></a>
					<a href="CexDis.jsp"><li><img src="images/left8.png">专家组任务分配</li></a>
					<br>
					<a href="Cproject_process_fallback.jsp"><li><img src="images/left9.png">项目流程回退</li></a>
					<a href="Cplace-on-file.jsp"><li class="now"><img src="images/left9-s.png">项目评分详情</li></a>
					<a href="CchangeSubPin.jsp"><li><img src="images/left2.png">下级人员密码修改</li></a>
					<a href="CchangePin.jsp"><li><img src="images/left9.png">修改个人密码</li></a>
				</ul>

			</div>	
			<div class="right">
				<div class="rhead">
				</div>
				<div class='ratemessage'></div>
				<div class='processRate'></div>
				<!-- 用于导出页面某项目所有学校的评分 -->
				<form id='exportScore' style='display:none' action="PlaceOnFileServlet/exportScore" method='post'>
					<input type='text' id='allScore' name='data' value=''>
				</form>
				<!-- 用于导出页面评分数据 -->
				<form id='exportXls' style='display:none' action="PlaceOnFileServlet/exportRate" method='post'>
					<input type='text' id='data' name='data' value=''>
				</form>
<!-- 				用于下载总评或附件 -->
				<form style='display:none' id='form2' method='post' action='DSPRateServlet/download'>
					<input type='text' name='location' value=''>
				</form>
				
				<form id='form1' method='post' action='PlaceOnFileServlet/rate' enctype="multipart/form-data">
					<span class="breakH" style='top: 125px;margin-left: 47px;width: 491px;'></span>
					<span class="breakV" style='top: 100px;margin-left: -139px;'></span>
					<div id='editorBg' style='background:#bfbfbf;width: 100%; height: 100%; z-index: 99119; position: fixed; top: 0px; left: 0px; opacity: 0.6;display:none'></div>
					<div id='editorBox' style='height: 76rem;margin-bottom: 2.9rem;z-index: 99999;width: 93rem;background-color: rgb(249, 249, 249);display: none;position: absolute;top: -72px;left: -19%;'>
					<input id='editorCancel' type="button" style="position: absolute;right: 5rem;top: 2.4rem;width: 9.5rem;border-radius: 10rem;height: 3rem;color: white;border: 0.1rem solid rgb(218, 220, 224);background-color: #53c2d6;" value="确定"/>
						<span style='display: block;padding-left: 5rem;font: 2.3rem "微软雅黑";color: black;padding-top: 2rem;padding-bottom: 2rem;border-bottom: 0.1rem #c3c3ba solid'>描述查看</span>
			<!-- 			编辑框 -->
					    <script id="editor" type="text/plain" style="    overflow-x: hidden;width: 82.5rem;margin: 0 auto;margin-top: 2.3rem;border-right: 0.1rem solid #d4d4d4;"></script>
					</div>
					<label id='message' style='    top: -320%;' class='message'><img src="images/warn.png" class='messageWarn'>提示消息</label>
					<div class="prType">
						<!-- 标识项目是否为总评或归档 -->
						<input id='doing' style='display:none;width: 20px;height: 15px;' type="radio" name='prType' value='exist'/><span style="display:none">在评项目</span>
						<input id='down' style='display:none;width: 20px;height: 15px;' type="radio" name='prType' value='end'/><span style="display:none">结束项目</span>
						
					</div>
					<div class='scoreType'>
						校：<span id='schScore'>未评分</span>
						县：<span id='distScore'>未评分</span>
						市：<span id='cityScore'>未评分</span>
					</div>
					<!-- 模拟项目动态下拉框 -->
					<!-- <select id='Pr' style="position:absolute;right: 68%;top: 181%;outline:none;"></select> -->
					<span style="position:absolute;right: 94%;top: 222%;margin-top:4px;">项目：</span>
					<input style='position: absolute;top: 50px;left: 63px;' type='text' id='prInput' list='Pr' />
					<datalist id='Pr'></datalist>
					<!-- 模拟学校动态下拉框 -->
					<!-- <select id='school' style="position:absolute;right: 35%;top: 181%;outline:none;"></select> -->
					<span style="position:absolute;right: 61%;top: 55%;top: 228%;margin-top:4px;">学校：</span>
					<input style='position: absolute;top: 52px;left: 381px;' type='text' id='schoolInput' list='school' />
					<datalist id='school'></datalist>
					
					<input type="button" style='right: 24%;top: 181%;' name="" id="uploadOverall" class='in3' value="查阅总评" onclick='lookDescri(this)'/>
					<input type="button" style='right: 13%;top: 181%;' class="in2" value="数据导出" onclick='exportRate();'/>
					<input type="button" style='right: 2%;top: 181%;background:#4bc1d1' name="" id="" class='in3' value="导出所有评分" onclick='exportScore(this)'/>
				</form>
				<div class="pagediv">
					<ul class="page">
					</ul>
				</div>

					<div id='tdiv' class='tdiv'>
					<table style='    width: 1289px;'>
  						<tr id='row1'>
							<th colspan='3' style='width:252px;background-color: rgb(236, 239, 253);z-index:3000;width: 155px;'></th>
							<th class='t4'  style='background-color: rgb(236, 239, 253);z-index:3000;'></td>
							<th class='t5'  style='background-color: rgb(236, 239, 253);z-index:3000;'></td>
							<th class='t5' style='background-color: rgb(236, 239, 253);z-index:3000;' ></td>
							<th class='t6 rightBreak' style='background-color: rgb(236, 239, 253);z-index:3000;'></td>
							<th class='t7'  style='background-color: rgb(236, 239, 253);z-index:3000;'></th>
							<th class='t9' style='z-index:2000;'></th>
							<th class='t8' style='text-indent:-174px;background-color: rgb(236, 239, 253);z-index:2000;' >县级专家</th>
							
							<th class='t7 widChan'  style='background-color: rgb(236, 239, 253);z-index:2000;'></th>
							<th class='t9' style='z-index:2000;'></th>
							<th class='t8' style='text-indent:-174px;background-color: rgb(236, 239, 253);z-index:2000;' >市级专家</th>
						</tr>
						<tr id='row2'>
							<th class='t1' style='z-index:3000;background-color: rgb(236, 239, 253);vertical-align: top;line-height: 1.5rem;'>A指标</td>
							<th class='t2' style='z-index:3000;background-color: rgb(236, 239, 253);vertical-align: top;line-height: 1.5rem;'>B指标</td>
							<th class='t3' style='z-index:3000;background-color: rgb(236, 239, 253);vertical-align: top;line-height: 1.5rem;'>C指标</td>
							<th class='t4' style='z-index:3000;background-color: rgb(236, 239, 253);vertical-align: top;line-height: 1.5rem;'>分值</td>
							<th class='t5' style='z-index:3000;background-color: rgb(236, 239, 253);vertical-align: top;line-height: 1.5rem;'>分数</td>
							<th class='t6' style='z-index:3000;background-color: rgb(236, 239, 253);vertical-align: top;line-height: 1.5rem;'>描述</td>
							<th class='t6 rightBreak' style='z-index:3000;background-color: rgb(236, 239, 253);vertical-align: top;line-height: 1.5rem;'>附件</td>
							<th class='t7' style='z-index:2000;'>分数</th>
							<th class='t9' style='z-index:2000;'>姓名</th>
							<th class='t8' style='z-index:2000;'>评分说明</th>
							
							<th class='t7 widChan' style='z-index:2000;'>分数</th>
							<th class='t9' style='z-index:2000;'>姓名</th>
							<th class='t8' style='z-index:2000;'>评分说明</th>
						</tr> 
					</table>
					<!-- 自定义滚动条 -->
					<div class="bottom-bar" style="width: 493px;left: 444px;top: 598px;">
		                <span></span>
		            </div>
					</div>
			</div>
		</div>
		<div class="footer">
			<span>&copy福建省教育厅教学项目评估系统版权所有.&nbsp;&nbsp;转载内容版权归作者及来源网站所有</span>
		</div>
	</div>
	</body>
</html>
<script type="text/javascript">
    //实例化编辑器
    //建议使用工厂方法getEditor创建和引用编辑器实例，如果在某个闭包下引用该编辑器，直接调用UE.getEditor('editor')就能拿到相关的实例
    var ue = UE.getEditor('editor', {
    	  enterTag : 'br',
		  wordCount:true,        
		  maximumWords:500,		  
    	  autoHeightEnabled: false,//是否自动长高
    	  autoFloatEnabled: false//是否保持toolbar的位置不动
    });
    $("#editor").css("height", '56rem');
    setTimeout(fun, 200);
    function fun() {
	    $("#edui1_bottombar").css("display", "none");
	    $("#edui1_message_holder").css("display", "none");
		ue.setDisabled('fullscreen');
    }
    function lookDescri(e) {
		$(e).css("backgroundColor","rgb(150, 132, 224, 0.5)");	
    	var descriContent = $(e).attr('name');
	    $("#editorBg").css("display", "block");
	    $("#editorBox").css("display", "block");
		ue.setContent(descriContent);
    }
    $("#editorCancel").click(function(){
	    $("#editorBg").css("display", "none");
	    $("#editorBox").css("display", "none");
    })
    
</script>
<script type="text/javascript">
//重写确认和提示框样式 
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
    			oTitle.style.left = (ev.pageX + 10) + 'px';
    			oTitle.style.top = (ev.pageY + 10) + 'px';
    			$(oTitle).css("zIndex", "5555");
    		};
    		elementTitle.onmousemove = function(ev) {
    			var ev = ev || window.event;
    			oTitle.style.left = (ev.pageX + 10) + 'px'; //获取鼠标所在x座标并增加10个像素,下同
    			oTitle.style.top = (ev.pageY + 10) + 'px';
    			$(oTitle).css("zIndex", "5555");
    		}
    		elementTitle.onmouseout = function() {
    			this.title = sTitle;
    			if(oTitle != null)
    				document.body.removeChild(oTitle);
    		}
    	}
    </script>
<!-- 自定义滚动条js -->
<script>
$(function() {
	bottomScroll('tdiv');
	function bottomScroll(id){
		var oBox = document.getElementById(id);
		// table
		var oCont = oBox.children[0];
		// scrollDiv
		var oDiv = oBox.children[1];
		// scrollDiv -> srollBar
		var oBar = oDiv.children[0];
		//滚动条的拖拽
		oBar.onmousedown = function(ev){
		    var	oEvent = ev || event;
		    var disX = oEvent.clientX - oBar.offsetLeft;
				
		    document.onmousemove = function(ev){
		        var oEvent = ev || event;
		        var t = oEvent.clientX - disX;
		        tab(t);
		    };
		    document.onmouseup = function(){
		        document.onmousemove = null;
		        document.onmouseup = null;
		        oBar.releaseCapture && oBar.releaseCapture();
		    };
			// 该函数在属于当前线程的指定窗口里设置鼠标捕获。一旦窗口捕获了鼠标，所有鼠标输入都针对该窗口，无论光标是否在窗口的边界内。同一时刻只能有一个窗口捕获鼠标。如果鼠标光标在另一个线程创建的窗口上，只有当鼠标键按下时系统才将鼠标输入指向指定的窗口
			// SetCapture()和ReleaseCapture()必须成对呈现
			// 你在一个窗口线程里对了了SetCapture()，但你在别的窗口的上点击了同样会把鼠标消息发个这个窗口而是我们通过调用SetCapture()设定那个窗口。因为当鼠标在窗口外面点击的时候，被点击的窗口获得焦点，原来的SetCapture()也就失效了
		    oBar.setCapture && oBar.setCapture();
		    return false;
		};
		//封装 滚动条和内容的高度
		function tab(t){
		    var maxWidth = oDiv.offsetWidth - oBar.offsetWidth;
		    if( t < 0){
		        t = 0;
		    }else if( t > maxWidth){
		        t = maxWidth;
		    }
		    var scale = t/maxWidth;
		    oBar.style.left = (t) + 'px';
			var cont_left = ((oBox.offsetWidth - oCont.offsetWidth)*scale) + 'px';
		    oCont.style.left = cont_left;
		    // 控制第一行前5个元素不动 
			nowLeft = cont_left.substring(cont_left.indexOf('-') + 1);
			var trs = $("table").find("tr:eq(0)");
			trs.each(function() {
				for(var i = 0 ; i <= 4; i++) {
					$(this).children().eq(i).css({
						"left": nowLeft,
					})
				}
			})
		    // 控制第2行前7个元素不动 
			var trs = $("table").find("tr:eq(1)");
			trs.each(function() {
				for(var i = 0 ; i <= 6; i++) {
					$(this).children().eq(i).css({
						"left": nowLeft,
					})
				}
			})
		    // 控制第2行之后所有行的前7个元素不动 
			var trs = $("table").find("tr:gt(1)");
			trs.each(function() {
				for(var i = 0 ; i <= 6; i++) {
					$(this).children().eq(i).css({
						"left": nowLeft
					})
				}
			})
		}
	}
})
</script>