<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head><meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<meta charset="utf-8">
		<title>学校自评</title>
		<link href="css/SSPRate.css" rel="stylesheet"/>
		<script src="js/jquery-3.3.1.min.js"></script>
		<script src="js/getWebSiteHead.js"></script>
		<script src="js/getWelcomeName.js"></script>
<!-- 		富文本编辑器 -->
		<script type="text/javascript" charset="utf-8" src="utf8-jsp/ueditor.config.js"></script>
 	 	<script type="text/javascript" charset="utf-8" src="utf8-jsp/ueditor.all.js"> </script>
		<script type="text/javascript" charset="utf-8" src="utf8-jsp/lang/zh-cn/zh-cn.js"></script>
		
		<script src="js/forbiddenFB.js"></script>
		<script src="js/forbiddenAuto.js"></script>
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
		<script>
		var pageReadonly = false;
		//记录未评分项 
		var noScore = {};
		//记录表格文件框和总评文件框个数 
		var uploadNum = {};
		//记录未描述的项
		var describe = {};
		//表示当前正在编辑的描述项 
		var nowDescribe = ""; 
		var imgHtml = "";
		var processData;
		var pid = "";
		// 重传之后应该被删除的文件
		var deleteFile = [];
		function messageDisplay() {
			$("#message").css("display", "inline-block");
		}
		function hid() {
			$("#message").css("display", "none");
		}
		var overExistFlag = false;
		var fileValue;
		//点击上传按钮，关联文件按钮 
		function clickUp(e) {
			var fileButtonName = $(e).prop("id");
			fileValue = $("input[type=file][name=" + fileButtonName + "]").val();
			$("input[type=file][name=" + fileButtonName + "]").click();
		}
		function showProcess(pData) {
			var data = pData;
			var flag = false;
			data.process = "-" + data.process;
			var split = '<img src="images/splitLine.png" class="split"/>';
			while(data.process.indexOf('-') != -1) {
				console.log(data.process);
				console.log(flag);
				var rank = data.process.charAt(1);// 忽略字符串开头的 '-'
				data.process = data.process.substring(2);// 去除字符串的 '-X'
				//获取当前循环所需的图片
				var img = '<img src="images/did.png" style="width: 39px" />';
				if(flag == true) { 
					img = '<img src="images/notStart.png" style="width: 39px" />';
					split = '<img src="images/splitLine.png" class="split"/>';
				}
				else if(rank == data.rate || (rank == "抽" && data.rate == "抽查")) {
					img = '<img src="images/doing.png" style="width: 39px" />';
					flag = true;
					split = '<img src="images/splitLine.png" class="split"/>';
				}
				if(data.rate == "结束") {
					img = '<img src="images/down.png" style="width: 39px" />';
					split = '<img src="images/splitLine.png" class="split"/>';
				}
				$("div[class=processRate]").append(img);
					
				if(rank == "校") {
					$("div[class=ratemessage]").append('<label style="margin-right: 72px">校级</label>');
				} else if(rank == "县") {
					$("div[class=ratemessage]").append('<label style="margin-right: 60px;">县（市、区）级</label>');
				} else if(rank == "市") {
					$("div[class=ratemessage]").append('<label style="margin-right: 95px;">设区市级</label>');
				} else if(rank == "省"){
					$("div[class=ratemessage]").append('<label style="margin-right: 95px;">省级</label>');
				} else if(rank == "抽") { 
					$("div[class=ratemessage]").append('<label>省级抽查</label>'); 
					break;
				}
				// 如果被上面的break，则不会重复输出一次
				if(data.process.indexOf('-') != -1) 
					$("div[class=processRate]").append(split);

			}
		}
			$(function() {
				//获取当前项目的进度 
				$.post("SSPRateServlet/findProcessRate", function(data) {
// 				<div class='ratemessage'>
// 					<label>学校申报</label>
// 					<label>区县评审</label>
// 					<label>地市评审</label>
// 					<label>省份评审</label>
// 				</div>
// 				<div class='processRate'>
// 					<img src="images/down.png" />
// 					<img src="images/splitLine.png" class='split'/>
// 					<img src="images/doing.png" />
// 					<img src="images/splitLine.png"  class='split'/>
// 					<img src="images/notStart.png" />
// 				</div>
					console.log(data);
					var str = JSON.stringify(data);
					processData = JSON.parse(str);
					showProcess(data);
				}, 'json')
				
				//固定表格头行 
				$(".tdiv").scroll(function() { //给table外面的div滚动事件绑定一个函数
					var top = $(".tdiv").scrollTop(); //获取滚动的距离
					//该行的所有单元格随着滚动 
					$("#Schooltable tr:eq(0)").children().each(function() {
						$(this).css({
							"position": "relative",
							"top": top,
							"left": "0px",
	// 						"background-color": "white",
							"z-index": "55"
						});
					})
				})
				
			    //查找页面待显示内容，即当前校级管理员指标内容 
			    $.post("SSPRateServlet/findPageBean", function(data) {
					console.log(data);
					pid = data.pid;
			    	if(data.message != undefined) {
						$("#message").html("<img src='images/warn.png' class='messageWarn'>" + data.message);
						messageDisplay();
						setTimeout(hid, 3000);
			    		return ;
			    	}
			    	if(data.flag == 11) {
			    		pageReadonly = true;
			    		setTimeout(funok, 100);			    		
			    	}
					dataShow(data);			    	
			    }, 'json')
			})
			//显示表格数据
			function dataShow(data) {
				uploadNum.overallReport = true;
				var location = "";
				// 显示总评 
				if(data.overallReport != null && data.overallReport.REPORTLOCATION != null && data.overallReport.REPORTLOCATION != "") {
					delete uploadNum.overallReport;
					overExistFlag = true;
					location = data.overallReport.REPORTLOCATION;
					imgHtml = location;
					$("#overallImgLocation").val(imgHtml);
					// 获取文件名，显示title 
					if(data.overallReport.overallFileName != null && data.overallReport.overallFileName != "") {
						var overFilePath = data.overallReport.overallFileName;
		    			$("#overallFileLocation").val(overFilePath);
		    			var fileTitle = overFilePath.substring(overFilePath.lastIndexOf('\\') + 1);
		    			var date = new Date();
		    			fileTitle = fileTitle + "<br>" + date.getFullYear() + "-" + date.getMonth() + "-" + date.getDate() + " "
	    				+ date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
		    			$("#uploadOverButton").prop("title", fileTitle);
		    			console.log($("#uploadOverButton")[0]);
		    			createTitleDiv($("#uploadOverButton")[0]);
					}
				}
				
				$("span[class=school]").append(data.mana.sname);
// 				<tr>
// 				<td class='t1'>A指标</td>
// 				<td class='t2'>B指标</td>
// 				<td class='t3'>C指标</td>
// 				<td class='t4'>5</td>
// 				<td class='t5'><input name='0C001' type='text' required="required"></td>
// 				<td class='t6'><input id='C001' type='button' value='上传' onclick="clickUp(this);"></td>
// 				<td style='display:none;'><input type='file' name='C001'></td>
// 			</tr>
				var tdTitleFileName = {};
				var aid = "",
					bid = "";
				for(var i = 0; i < data.tableData.length ; i++) {
					trs = "<tr>";
					if(aid != data.tableData[i].aid)
						aid = data.tableData[i].aid,
						trs += "<td class='t1' title='" + data.tableData[i].aname + "'>" + data.tableData[i].aname + "</td>";
					else 
						trs += "<td class='t1'></td>";
					if(bid != data.tableData[i].bid)
						bid = data.tableData[i].bid,
						trs += "<td class='t2' title='" + data.tableData[i].bname + "'>" + data.tableData[i].bname + "</td>";
					else  trs += "<td class='t2'></td>";
					trs += "<td class='t3' title='" + data.tableData[i].cname + "'>" + data.tableData[i].cname + "</td>";
					trs += "<td calss='t4'>" + data.tableData[i].score + "</td>";
					if(data.tableData[i].SCHOOLSCORE == null || data.tableData[i].SCHOOLSCORE == -1) {
						noScore[data.tableData[i].cid] = true;						
						trs += "<td class='t5' title='分段值:" + data.tableData[i].segscore + "'><input class='" + data.tableData[i].segscore + "' name='0" + data.tableData[i].cid + "' type='text' required='required'></td>";
					}
					else 
						trs += "<td class='t5' title='分段值:" + data.tableData[i].segscore + "'><input class='" + data.tableData[i].segscore + "' name='0" + data.tableData[i].cid + "' type='text' required='required' value='" + data.tableData[i].SCHOOLSCORE + "'></td>";
					if(data.tableData[i].description != null && data.tableData[i].description != "")
						trs += "<td class='t15'><input id='1" + data.tableData[i].cid + "' type='button' style='background-color: rgba(150, 132, 224, 0.5);' value='编辑'></td>";
					else 
						trs += "<td class='t15'><input id='1" + data.tableData[i].cid + "' type='button' value='编辑'></td>";
					if(data.tableData[i].isannex == 'TRUE' || data.tableData[i].isannex == 'true') {
						if(data.tableData[i].ANNEXLOCATION == null || data.tableData[i].ANNEXLOCATION == "") {
							uploadNum[data.tableData[i].cid] = true;
							trs += "<td class='t6'><input id='" + data.tableData[i].cid + "' type='button' value='上传' onclick='clickUp(this);'></td>";
						}
						else {
							var url = data.tableData[i].ANNEXLOCATION;
							tdTitleFileName[data.tableData[i].cid] = url.substring(url.lastIndexOf('\\') + 1);
							trs += "<td class='t6'><input style='background-color: rgba(150, 132, 224, 0.5);' id='" + data.tableData[i].cid + "' type='button' value='重传' onclick='clickUp(this);'></td>";
// 							trs += "<td class='t6' title='" + url.substring(url.lastIndexOf('\\') + 1) + "' ><input style='background-color: rgba(150, 132, 224, 0.5);' id='" + data.tableData[i].cid + "' type='button' value='重传' onclick='clickUp(this);'></td>";
						}
					}
					else trs +=  "<td class='t6'></td>";
					
					trs += "<td style='display:none;'><input type='file' name='" + data.tableData[i].cid + "'></td>";
					if(data.tableData[i].description != null && data.tableData[i].description != "")
						trs += "<td style='display:none;'><input type='text' name='1" + data.tableData[i].cid + "' value='" + data.tableData[i].description + "'></td>";
					else {
						describe[data.tableData[i].cid] = 'true';
						trs += "<td style='display:none;'><input type='text' name='1" + data.tableData[i].cid + "'></td>";
					}
					trs += "</tr>";
					$("#Schooltable").append(trs);
				}
				
				for(var key in tdTitleFileName) {
					$("#" + key).parent().prop('title', tdTitleFileName[key]);
					console.log(key + " : " + tdTitleFileName[key]);
				}
				
				$("input[id^=1]").click(function() {

					$("#charLimit").css("display","inline-block");
					nowClickuploadOver = false;
					nowDescribe = $(this).prop("id");
					$("#editorBox").css("display", "block");
					$("#editorBg").css("display", "block");
					if(pageReadonly == false)
			        	UE.getEditor('editor').focus();
					UE.getEditor('editor').setContent($("input[name=" + nowDescribe + "]").val());
				})	
				//已经提交过，不可重传 
				//总评不可上传 
				//已经提交过，文本只读 
				if(pageReadonly == true) {
					$(":input").css("pointer-events", "none"); 
					$(":input[value='编辑']").unbind(); 
					$(":input[value='退出登录']").css("pointer-events", "auto"); 
					$("#uploadOverall").unbind();
					$("#uploadOverButton").removeAttr('onclick');
					$(":input").prop("readonly", true);
				}
				//消除文本框的自动提示 
				$("input").prop("autocomplete", "off");
				//文本完成时判断是否合规格 
				$("input[type=text]").blur(function() {
					console.log("here" + $(this).prop("name"));
					// 描述框改变
					if($(this).prop("name").charAt(0) == 1) {
						if($(this).val() != "") 
							delete describe[$(this).prop("name").substring(1)];
						else 
							describe[$(this).prop("name").substring(1)] = 'true';
						return ;
					}
					// 分数框改变
					var num = $(this).prop("class").substring($(this).prop("class").lastIndexOf('.') + 1).length;
					if((Math.pow(10, num) * $(this).val()) % (Math.pow(10, num) * $(this).prop("class")) != 0) {
						$("#message").html("<img src='images/warn.png' class='messageWarn'>" + "该指标的分段值为:" + $(this).prop('class') + " 请输入有效分值");
						messageDisplay();
						setTimeout(hid, 3000);
						$(this).val("");
					}
					var score = $(this).parent().parent().find("td:eq(3)").text();
					if(parseFloat(score) < parseFloat($(this).val()) || parseFloat($(this).val()) < 0) {
						$("#message").html("<img src='images/warn.png' class='messageWarn'>" + "自评分数无效");
						messageDisplay();
						setTimeout(hid, 3000);
						$(this).val("");
					}
					delete noScore[$(this).prop("name").substring(1)];
				})
				titleShow();
				
			    $("#Schooltable input[type=file]").change(function(){
			    	if(parseInt(this.files[0].size) >= 5242880) {
			    		console.log(fileValue);
			    		// 每一次上传不是回到原来的状态，而是清空文件。则在单次上传时，上传小于5M（成功） -> 上传超过5M（清空）。此时虽然页面显示小于5M的文件，但是小于5M的文件已经被清空了，会让用户以为5M的文件仍然存在。
			    		$(this).val("");
			    		zdalert("错误提示", "上传附件需小于5M");
			    		return ;
			    	}
			    	
			    	// 记录重传之前的文件名
			    	deleteFile.push(sTitle);
			    	var fileUrl = $(this).val();
			    	if(fileUrl == "") 
			    		return ;
					delete uploadNum[$(this).prop("name")];
			    	var fileButtonId = $(this).prop("name");
			    	//更换title标题，显示新文件名 
			    	var fileName = fileUrl.substring(fileUrl.lastIndexOf('\\') + 1);
			    	var date = new Date();
			    	var month = parseInt(date.getMonth()) + 1;
			    	if (month < 10) month = "0" + month;
			    	fileTitle = fileName + "<br>" + date.getFullYear() + "-" + month + "-" + date.getDate() + " "
			    				+ date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
			    	sTitle = fileTitle;
			    	$("#" + fileButtonId).parent().prop("title", fileName);
					titleShow();
					$("#" + fileButtonId).val("重传");
					$("#" + fileButtonId).css("backgroundColor","rgb(150, 132, 224, 0.5)");
			    })
			    //上传总评 
			    $("#overall").change(function() {
					delete uploadNum.overallReport;
			    })
			    //赋值时有异常，故放在最后给富文本框赋值 
			    if(location != null && location != "")
			    	ue.setContent(location);
			}
			function saveDraft() {
			    if(pageReadonly == true) //未提交过，可以编辑 
			    	return ;
				console.log("clickDraft");
 				$("#message").html("<img src='images/warn.png' class='messageWarn'>" + "材料正在保存...");
 				messageDisplay(); 
 				setTimeout(hid, 5000);
 			    // 删除重传之前的文件
 			    $.post("SSPRateServlet/deleteFile", {"deleteFile": JSON.stringify(deleteFile)}, function() {
 			    	deleteFile = [];
 			    	// 开始保存
 	 				var targetUrl = "SSPRateServlet/saveDraft";    
 	 			    var form1 = new FormData($( "#form1" )[0]);
 	 			    $.ajax({
 	 				     type:'post',  
 	 				     url:targetUrl, 
 	 				     cache: false,    //上传文件不需缓存，缓存只在get请求有效，缓存回传信息 
 	 				     processData: false, //需设置为false。因为data值是FormData对象，不需要对数据做处理
 	 				     contentType: false, //需设置为false。因为是FormData对象，且已经声明了属性enctype="multipart/form-data"
 	 				     data: form1,
 	 				     dataType: 'json',
 	 				     success:function(data) { 
 	 						$("#message").html("<img src='images/warn.png' class='messageWarn'>" + data.message);
 	 						messageDisplay();
 	 						setTimeout(hid, 3000);
 	 				     }
 	 			    });
 			    })
			}
			//异步提交表单，确定评分 
			function rate() {
				if(pageReadonly == true) //未提交过，可以编辑 
		    		return ;
				if(Object.keys(uploadNum).length != 0) {
					$("#message").html("<img src='images/warn.png' class='messageWarn'>" + "请上传所有必须的附件");
					messageDisplay();
					setTimeout(hid, 3000);
					return ;
				} 
				if(Object.keys(noScore).length != 0) {
					$("#message").html("<img src='images/warn.png' class='messageWarn'>" + "请填写所有分数后，再提交考核");
					messageDisplay();
					setTimeout(hid, 3000);
					return ;	
				}
				if(Object.keys(describe).length != 0) {
					$("#message").html("<img src='images/warn.png' class='messageWarn'>" + "请填写所有描述后，再提交考核");
					messageDisplay(); 
					setTimeout(hid, 3000);
					return ;
				}
				$(".in2").removeAttr('onclick');
				$("#uploadOverButton").removeAttr('onclick');
			      zdconfirm('系统确认框',"提交后，自评信息将不可修改、无法回退。请确定是否提交？",function(r){ //项目删除确定框 
			    	     if(r) {
			 				$("#message").html("<img src='images/warn.png' class='messageWarn'>" + "资料正在上传...");
			 				messageDisplay();
			 				setTimeout(hid, 3000);
			 				var targetUrl = $("#form1").attr("action");    
			 			    var form1 = new FormData($( "#form1" )[0]);
			 			    // 删除重传之前的文件
			 			    $.post("SSPRateServlet/deleteFile", {"deleteFile": JSON.stringify(deleteFile)}, function() {
			 			    	deleteFile = [];
								// 发送提交
				 			    $.ajax({
				 				     type:'post',  
				 				     url:targetUrl, 
				 				     cache: false,    //上传文件不需缓存，缓存只在get请求有效，缓存回传信息 
				 				     processData: false, //需设置为false。因为data值是FormData对象，不需要对数据做处理
				 				     contentType: false, //需设置为false。因为是FormData对象，且已经声明了属性enctype="multipart/form-data"
				 				     data: form1,
				 				     dataType: 'json',
				 				     success:function(data) {
				 						$("#message").html("<img src='images/warn.png' class='messageWarn'>" + data.message);
				 						messageDisplay();
				 						setTimeout(hid, 3000);
				 						if(data.message != "上传成功") {
					 					    $(".in2").bind("click", rate);
					 					    $("#uploadOverButton").bind("click", uploadOverButtonClick);
				 							return ;
				 						}
				 						else {
				 							overExistFlag = true;
					 						pageReadonly = true;
					 						$("input").prop("readonly", true);
					 					    setTimeout(funok, 100);
					 					    $("#uploadOverall").unbind();
					 						$("input").unbind();
					 					   $("div[class=processRate]").html("");
					 					   $("div[class=ratemessage]").html("");
					 					   processData.rate = processData.process.charAt(2);
					 					   showProcess(processData);
				 						}
				 				     }
				 			    });
			 			    })
			    	      }
		    	    });
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
			<div class="location">当前位置：主页&nbsp;&nbsp;>&nbsp;&nbsp;学校自评</div>
			<span style="position: absolute;font-size: 20px;top: 12%;left: 31%;color: grey;">提示：每个附件文件大小不超过5M，总附件大小不超过80M</span>
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
					<span class="s2"><span class='welcomeName' style='display: flex;width: 120px;'></span>
					</li>
					<a href="#"><li class="now"><img src="images/left3-s.png">学校自评</a></li>
					<a href="SPChangePin.jsp"><li><img src="images/left2.png">修改用户信息</li></a>
				</ul>
			</div>
			<div class="right">
				<div class="rhead">
				</div>
				<div class='ratemessage'>
<!-- 					<label>学校申报</label> -->
<!-- 					<label>区县评审</label> -->
<!-- 					<label>地市评审</label> -->
<!-- 					<label>省份评审</label> -->
				</div>
				<div class='processRate'>
<!-- 					<img src="images/down.png" /> -->
<!-- 					<img src="images/splitLine.png" class='split'/> -->
<!-- 					<img src="images/doing.png" /> -->
<!-- 					<img src="images/splitLine.png"  class='split'/> -->
<!-- 					<img src="images/notStart.png" /> -->
				</div>
<!-- 				提交总评文件 -->
				<form id='form2' method='post' action='SSPRateServlet/glanceOverall' enctype="multipart/form-data" style='display:none' >
					<input type='file' name='overallDocFile' id='overallDocFile' accept=".doc,.docx"/>
				</form>
				<form id='form1' method='post' action='SSPRateServlet/rate' enctype="multipart/form-data">
				<input id='draft' class='draft' type='button' value='保存草稿' onclick='saveDraft();'/>
<!-- 					存放总评文件在服务器的地址 -->
					<input type='text' id='overallImgLocation' name='overallImgLocation' style='display:none' />
					<input type='text' id='overallFileLocation' name='overallFileLocation' style='display:none' />
<!-- 					富文本输入框背景 --> 
					<div id='editorBg' style='background:#bfbfbf;width: 100%; height: 100%; z-index: 999; position: fixed; top: 0px; left: 0px; opacity: 0.6;display:none'></div>
					<div id='editorBox' style="left: 7%; height: 76rem; margin-bottom: 2.9rem; z-index: 99999; width: 93rem; background-color: rgb(249, 249, 249);display: none;position: absolute;top: -13rem;left: -18%;">
						<input onclick="uploadOverallClick()" id='uploadOverall' type="button" class='uploadOverall' style='position: absolute;right: 29.2rem;top: 1.1rem;display:none;width: 9.5rem;' value="上传附件"/>
						<input onclick='editorSaveClick()' id='editorSave' type="button" style='position: absolute;right: 17rem;top: 1.8rem;border-radius:0px;margin-top:1rem;margin-left: 5rem;width: 9.5rem;    border-radius: 10rem;height: 3rem; color: white; border: none; background-color: #5262c4;' value="保存"/>
						<input onclick='editorCancelClick()' id='editorCancel' type="button" style="margin-top:1rem;margin-left: 5rem;width: 9.5rem;    border-radius: 10rem;height: 3rem; color: white; border: 0.1rem solid rgb(218, 220, 224);background-color: #53c2d6;position: absolute;right: 5rem;top: 1.8rem;" value="取消"/>
						<span style='display: block;padding-left: 5rem;font: 2.3rem "微软雅黑";color: black;padding-top: 2rem;padding-bottom: 2rem;border-bottom: 0.1rem #c3c3ba solid'>描述编辑</span>
						<label id='charLimit' style="position: absolute;left: 14rem;top: 3.8rem;">(请输入不超过500字符的描述)</label>
			<!-- 			编辑框 -->
					    <script id="editor" type="text/plain" style="    overflow-x: hidden;width: 82.5rem;margin: 0 auto;margin-top: 2.3rem;border-right: 0.1rem solid #d4d4d4;"></script>
			<!-- 		    保存或取消按钮 -->
					</div> 
					<label id='message' class='message'><img src="images/warn.png" class='messageWarn'>提示消息</label>
<!-- 					<span class="s1" style='margin-top: 2rem;'>欢迎您！ -->
<!-- 					</span> -->
<!-- 					<span class="s2">  -->
<!-- 						<span class='school'></span> -->
<!-- 					</span> -->
					<input type="button" onclick='uploadOverButtonClick()' name="uploadOverButton" id="uploadOverButton" class="in3" value="上传总评"/>
<!-- 				用于上传总评 -->
<!-- 					<input style='display:none' name='overallFile' id='overall' type='file'> -->
					<input type="button" name="" class="in2" style='margin-right:13.2rem' value="提交" onclick='rate();'/>
					<div class='tdiv'>
					<table id='Schooltable'>
						<tr>
							<th class='t1'>A指标</td>
							<th class='t2'>B指标</td>
							<th class='t3'>C指标</td>
							<th class='t4'>分值</td>
							<th class='t5'>分数</td>
							<th class='t15'>描述</td>
							<th class='t6'>附件</td>
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
     
        $("#mb_msg").css({ padding: '2rem 0px 2rem 13px', lineHeight: '2rem', 'padding-left': '13px',
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
<script type="text/javascript">
// 监听文本改变事件，字符不可超过500字
function ueChange() {
	if(ue.getContentTxt().length > 500) {
		// 这样子处理，原来有格式的数据会丢失格式
		/*
		var content = ue.getContentTxt().substring(0,500);
		ue.setContent(content);
		ue.blur(); 
		*/
		
		var content = UE.getEditor('editor').getPlainTxt();
		content = content.replace(/\n/gm, "<br>");
		var charNum = 1;
		var subStr = "";
		for(var i = 0; i < content.length && charNum <= 500; i++) {
			if(content[i] == '&') {
				subStr = content.substring(i, i + 5);
				console.log(subStr);
				if(subStr == '&nbsp')
					i+=4;
			} else if(content[i] == '<') {
				subStr = content.substring(i, i + 4);
				console.log(subStr);
				if(subStr == '<br>')
					i+=3;
			} else
				++charNum;
		}
		content = content.substring(0, i);
		ue.setContent(content);
		ue.blur(); 
	}
		
}
    //实例化编辑器
    //建议使用工厂方法getEditor创建和引用编辑器实例，如果在某个闭包下引用该编辑器，直接调用UE.getEditor('editor')就能拿到相关的实例
    var ue = UE.getEditor('editor', {
//     	  initialFrameWidth:null ,//宽度随浏览器自适应
//     	  wordCount: false, //关闭字数统计
//     	  elementPathEnabled : false,//隐藏元素路径
    	  enterTag : 'br',
		  wordCount:true,        
		  maximumWords:500,		  
    	  autoHeightEnabled: false,//是否自动长高
    	  autoFloatEnabled: false//是否保持toolbar的位置不动
    });
    // 监听文本改变，限制500字
    ue.addListener("contentChange",ueChange);
    
    $("#editor").css("height", '56rem');
    setTimeout(fun, 200);
    function fun() {
	    $("#edui1_bottombar").css("display", "none");
	    $("#edui1_message_holder").css("display", "none");
	    if(pageReadonly == true) //已经提交过，不可编辑 
			ue.setDisabled('fullscreen');
    }
</script>
<script type="text/javascript">

var nowClickuploadOver = false;
var nowOverallHtml = "";
var oldOverall = {};
function uploadOverButtonClick() {
	nowClickuploadOver = true;
	$("#uploadOverall").css("display", "inline-block");
	if(pageReadonly == false)
    	UE.getEditor('editor').focus();
	$("#editorBg").css("display", "block");
	$("#editorBox").css("display", "block");
	$("#editorSave").css("margin-left", "5rem");
	$("#editorBox > span").text("总评浏览");
	$("#charLimit").css("display","none");
    setTimeout(funok, 100);
	console.log(imgHtml);
	if(imgHtml != "" && imgHtml != null)
		ue.setContent(imgHtml);
}
function funok() {
	ue.setDisabled('fullscreen');
}
// 点击上传总评 
function uploadOverallClick() {
	$("#overallDocFile").click();
}
// 提交并访问总评浏览内容 
$("#overallDocFile").change(function() {
	// 在传送过程中禁用退出按钮、和保存按钮
	$("#uploadOverall").removeAttr("onclick");
	$("#editorSave").removeAttr("onclick");
	$("#editorCancel").removeAttr("onclick");
	var fileUrl = $(this).val();
	if($(this).val() =="")
		return ;
	var targetUrl = $("#form2").attr("action");    
	    var form1 = new FormData($( "#form2" )[0]);
	    $.ajax({
		     type:'post',  
		     url:targetUrl, 
		     cache: false,    //上传文件不需缓存，缓存只在get请求有效，缓存回传信息 
		     processData: false, //需设置为false。因为data值是FormData对象，不需要对数据做处理
		     contentType: false, //需设置为false。因为是FormData对象，且已经声明了属性enctype="multipart/form-data"
		     data: form1,
		     dataType: 'json',
		     success:function(data) {
		    	 // 传送完成开启三个按钮的使用
				$("#uploadOverall").attr("onclick", "uploadOverallClick()");
				$("#editorSave").attr("onclick", "editorSaveClick()");
				$("#editorCancel").attr("onclick", "editorCancelClick()");
		    	 console.log("传送成功");
		    	 console.log(data);
		    	 if(data.message != undefined) {
					$("#message").html("<img src='images/warn.png' class='messageWarn'>" + data.message);
					messageDisplay();
					setTimeout(hid, 3000);
					return ;    		 
		    	 } else {
		    		 //保存路径到总评表中 
		    		 imgHtml = "<img src='" + data.imgUrl +"' />";
		    		 oldOverall.imgHtml =$("#overallImgLocation").val(); 
		    		 oldOverall.FileLocation = $("#overallFileLocation").val();
		    		 oldOverall.overButtonTitle = $("#uploadOverButton").prop("title");
	    			$("#overallImgLocation").val(imgHtml);
					$("#editorBg").css("display", "block");
					$("#editorBox").css("display", "block");
			        UE.getEditor('editor').focus();
			    	var fileName = fileUrl.substring(fileUrl.lastIndexOf('\\') + 1);
			    	var date = new Date();
			    	fileTitle = fileName + "<br>" + date.getFullYear() + "-" + date.getMonth() + "-" + date.getDate() + " "
			    				+ date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
			    	sTitle = fileTitle;
	    			$("#overallFileLocation").val(data.filePath);
	    			$("#uploadOverButton").prop("title", fileTitle);
	    			createTitleDiv($("#uploadOverButton")[0]);
	    			console.log($("#uploadOverButton")[0]);
					//改变生成的div样式
		    		 UE.getEditor('editor').setContent(imgHtml); 
		    	 }
		     },
		     error:function() {
		    	 // 传送完成开启三个按钮的使用
				$("#uploadOverall").attr("onclick", "uploadOverallClick()");
				$("#editorSave").attr("onclick", "editorSaveClick()");
				$("#editorCancel").attr("onclick", "editorCancelClick()");
		     }
	    });
	    console.log("传送中");
})

function editorSaveClick() {
	
	$("#uploadOverall").css("display", "none");
	$("#editorBox > span").text("描述编辑"); 
	$("#editorSave").css("margin-left", "5rem");
	$("#editorBox").css("display", "none");
	$("#editorBg").css("display", "none");

    if(pageReadonly == true) //未提交过，可以编辑 
    	return ;
    if(pageReadonly == false) //未提交过，可以编辑 
    	setTimeout(enable, 200);
    setTimeout(clearEditor, 100);
    if(nowClickuploadOver == true) {
    	delete uploadNum.overallReport;
		 oldOverall.imgHtml = $("#overallImgLocation").val(); 
		 oldOverall.FileLocation = $("#overallFileLocation").val();
		 oldOverall.overButtonTitle = $("#uploadOverButton").prop("title");
    	return ;
    }
	var content = UE.getEditor('editor').getPlainTxt();
	content = content.replace(/\n/gm, "<br>");
	var charNum = 1;
	var subStr = "";
	for(var i = 0; i < content.length && charNum <= 500; i++) {
		if(content[i] == '&') {
			subStr = content.substring(i, i + 5);
			console.log(subStr);
			if(subStr == '&nbsp')
				i+=4;
		} else if(content[i] == '<') {
			subStr = content.substring(i, i + 4);
			console.log(subStr);
			if(subStr == '<br>')
				i+=3;
		} else
			++charNum;
	}
	content = content.substring(0, i);
	console.log(content);
    if(content == "" || content == "<br>") {
    	$("input[name=" + nowDescribe + "]").val("");
    	$("input[name=" + nowDescribe + "]").blur();
    	$("input[id=" + nowDescribe + "]").css("backgroundColor", "#455fe7");    	
    	return ;
    }
	setTimeout(changeInput, 300);
	$("input[id=" + nowDescribe + "]").css("backgroundColor","rgb(150, 132, 224, 0.5)");
	$("input[name=" + nowDescribe + "]").val(content);
	nowClickuploadOver = false;
}
function clearEditor() {
    UE.getEditor('editor').setContent(''); 
}
function changeInput() {
	console.log("change");
	$("input[name=" + nowDescribe + "]").blur();
}
function enable() {
    UE.getEditor('editor').setEnabled();
}
function editorCancelClick() {
	if(nowClickuploadOver == true) {
		if(overExistFlag != true) {
			//返回上传的修改 
			imgHtml = oldOverall.imgHtml;
		  	$("#overallImgLocation").val(oldOverall.imgHtml); 
			$("#overallFileLocation").val( oldOverall.FileLocation);
	    	sTitle = oldOverall.overButtonTitle;
			$("#uploadOverButton").prop("title", sTitle);
			createTitleDiv($("#uploadOverButton")[0]);
			$("#overallDocFile").val("");
		}
	}
	$("#uploadOverall").css("display", "none");
	$("#editorBox > span").text("描述编辑"); 
	$("#editorBox").css("display", "none");
	$("#editorBg").css("display", "none");
    if(pageReadonly == false) //未提交过，可以编辑 
  	  setTimeout(enable, 100);
    setTimeout(clearEditor, 100);
	 
// 	$("input[name=" + nowDescribe + "]").val(""); 
// 	$("input[id=" + nowDescribe + "]").css("backgroundColor", "#455fe7");
// 	$("input[name=" + nowDescribe + "]").blur(); 
    nowClickuploadOver = false; 
}
</script>